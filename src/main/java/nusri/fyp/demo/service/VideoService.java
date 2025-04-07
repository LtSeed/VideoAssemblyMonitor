package nusri.fyp.demo.service;

import lombok.extern.slf4j.Slf4j;
import nusri.fyp.demo.repository.RoboflowWorkflowRepository;
import nusri.fyp.demo.service.img_sender.ImageSenderService;
import nusri.fyp.demo.service.img_sender.python.ImageSenderServiceImplOfPython;
import nusri.fyp.demo.service.img_sender.roboflow.ImageSenderServiceImplOfRoboflow;
import nusri.fyp.demo.state_machine.AbstractActionObservation;
import nusri.fyp.demo.state_machine.StateMachine;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <b>Service class for handling video uploads, processing video frames, and interacting with the state machine.</b>
 * <br> This class provides functionalities to:
 * <ul>
 *     <li>Upload and save video files.</li>
 *     <li>Process uploaded videos by sending frames for recognition.</li>
 *     <li>Manage recognition results and integrate them with a {@link StateMachine}.</li>
 *     <li>Handle single-frame (image) processing for real-time recognition updates.</li>
 * </ul>
 * <br>
 * <p>
 * This service works in close association with:
 * <br> - {@link ConfigService} for retrieving model configurations and file paths.
 * <br> - {@link ImageSenderServiceImplOfPython} and {@link ImageSenderServiceImplOfRoboflow} for frame/image processing.
 * <br> - {@link StateMachineService} for integrating recognition results into the state machine.
 * <br> - {@link RoboflowWorkflowRepository} for retrieving workflow IDs in a Roboflow environment.
 * </p>
 * @author Liu Binghong
 * @since 1.0
 * @see ConfigService
 * @see StateMachineService
 * @see ImageSenderService
 * @see ImageSenderServiceImplOfPython
 * @see ImageSenderServiceImplOfRoboflow
 * @see RoboflowWorkflowRepository
 */
@Service
@Slf4j
public class VideoService {

    private final ConfigService configService;
    private final ImageSenderServiceImplOfPython imageSenderServiceImplOfPython;
    private final ImageSenderServiceImplOfRoboflow imageSenderServiceImplOfRoboflow;
    private final StateMachineService stateMachineService;
    private final RoboflowWorkflowRepository roboflowWorkflowRepository;

    /**
     * Constructs the {@link VideoService} with the required dependencies for video processing.
     * <br> These dependencies include {@link ConfigService}, two different implementations of
     * {@link ImageSenderService}, the {@link StateMachineService}, and the {@link RoboflowWorkflowRepository}.
     *
     * @param configService                the configuration service for retrieving file paths and model info
     * @param imageSenderServiceImplOfPython the Python-based implementation of image sending
     * @param imageSenderServiceImplOfRoboflow the Roboflow-based implementation of image sending
     * @param stateMachineService          the service for managing state machines
     * @param roboflowWorkflowRepository   the repository for Roboflow workflow data
     * @see ConfigService
     * @see ImageSenderServiceImplOfPython
     * @see ImageSenderServiceImplOfRoboflow
     * @see StateMachineService
     * @see RoboflowWorkflowRepository
     */
    public VideoService(ConfigService configService,
                        ImageSenderServiceImplOfPython imageSenderServiceImplOfPython,
                        ImageSenderServiceImplOfRoboflow imageSenderServiceImplOfRoboflow,
                        StateMachineService stateMachineService,
                        RoboflowWorkflowRepository roboflowWorkflowRepository) {
        this.configService = configService;
        this.imageSenderServiceImplOfPython = imageSenderServiceImplOfPython;
        this.imageSenderServiceImplOfRoboflow = imageSenderServiceImplOfRoboflow;
        this.stateMachineService = stateMachineService;
        this.roboflowWorkflowRepository = roboflowWorkflowRepository;
    }

    /**
     * Processes an uploaded video by sending frames to the selected model for recognition.
     * <br> The frames are retrieved from a temporary file maintained by the relevant {@link ImageSenderService}.
     * <br> The results are stored as observations in the {@link StateMachine} corresponding to the given user and preset.
     * <br>
     * The method spawns a separate thread to handle the frame-by-frame processing:
     * <ul>
     *     <li>The observations are stored in an {@link AtomicReference}.</li>
     *     <li>The thread is then joined to ensure processing completes before returning.</li>
     *     <li>The observations are attached to the user's {@link StateMachine} instance.</li>
     * </ul>
     *
     * @param user       the user identifier, used to distinguish different users' sessions
     * @param presetName the preset name corresponding to the desired model/preset configuration
     * @return a {@link ResponseEntity} containing the resulting observations or an error message in case of failure
     * @see #buildSenderService(String)
     * @see #buildConfig(String)
     * @see StateMachineService#getStateMachineByName(String)
     */
    public ResponseEntity<? extends Serializable> processVideo(String user, String presetName) {
        log.info("user start to process video: {}", user);
        String modelWithConfig = configService.getUseModel(presetName);
        ImageSenderService imageSenderService = buildSenderService(modelWithConfig);
        Map<String, String> config = buildConfig(modelWithConfig);

        if (!imageSenderService.tempFiles.containsKey(user)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No file uploaded");
        }

        // Thread-safe container for storing the results (observations) of video processing
        AtomicReference<Map<Long, List<AbstractActionObservation>>> observations = new AtomicReference<>();

        // Thread creation and start
        Thread thread = new Thread(() -> {
            try {
                Map<Long, List<? extends AbstractActionObservation>> longListMap =
                        imageSenderService.sendVideoFile(imageSenderService.tempFiles.get(user), user, config);

                Map<Long, List<AbstractActionObservation>> listMap = new TreeMap<>();
                longListMap.forEach((key, value) ->
                        listMap.put(key, value.stream().map(a -> (AbstractActionObservation) a).toList())
                );
                observations.set(listMap);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        log.info("user start to process video- thread.start();: {}", user);
        thread.start();
        // Keep track of the thread in case we need to interrupt processing later
        stateMachineService.addProcess(user, thread);

        try {
            thread.join();
            // Return the observations from the image sender
            if (observations.get() == null || observations.get().isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing video");
            }
            // Initialize or restart the state machine and attach observations
            stateMachineService.start(user, presetName);
            stateMachineService.getStateMachineByName(user).setObservations(observations.get());
            log.info("user start to process video- return ResponseEntity.ok(observations);: {}", user);
            return ResponseEntity.ok(observations);
        } catch (InterruptedException | NullPointerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Interrupted by user");
        }
    }

    /**
     * Uploads and saves a video file to a designated path, then resets the relevant services.
     * <br> The user's existing {@link StateMachine} (if any) is stopped, and all ongoing image-sending processes are interrupted.
     * <br> The video file is saved under a fixed name in the configured location, using the user identifier.
     *
     * @param videoFile the uploaded {@link MultipartFile} containing video data
     * @param user      the user identifier
     * @return a {@link ResponseEntity} representing the outcome of the save operation
     * @throws IOException if an I/O error occurs during file handling
     * @see #isValidFilename(String)
     */
    public ResponseEntity<String> uploadAndSave(MultipartFile videoFile, String user) throws IOException {
        stateMachineService.stopStateMachine(user);
        imageSenderServiceImplOfPython.interrupt(user);
        imageSenderServiceImplOfRoboflow.interrupt(user);

        if (videoFile.isEmpty()) {
            log.error("video file is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No file uploaded");
        }

        // Determine the destination path for the uploaded file
        String destinationPath = configService.getVideoPath() + File.separator + user + ".mp4";
        if (!isValidFilename(user)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid video file name");
        }

        // Save the file to disk
        File destinationFile = new File(destinationPath);
        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(destinationFile))) {
            stream.write(videoFile.getBytes());
            stream.flush();
        }
        log.info("Saved video file to {}", destinationFile.getAbsolutePath());

        // Update references for Python-based and Roboflow-based services
        imageSenderServiceImplOfPython.tempFiles.put(user, destinationFile);
        imageSenderServiceImplOfRoboflow.tempFiles.put(user, destinationFile);

        return null;
    }

    /**
     * Validates the given filename to ensure it does not contain invalid characters or reserved names.
     * <br> This is important particularly on Windows-based systems with specific filename constraints.
     *
     * @param name the filename to validate
     * @return {@code true} if the filename is valid, otherwise {@code false}
     */
    public static boolean isValidFilename(String name) {
        if (name == null || name.trim().isEmpty()) {
            log.error("Invalid video file name: name is null or empty");
            return false;
        }

        // Check for invalid characters
        String invalidChars = "[\\\\/:*?\"<>|]";
        if (name.matches(".*" + invalidChars + ".*")) {
            log.error("Invalid video file name: name contains invalid characters");
            return false;
        }

        // Check against Windows reserved names
        String[] reservedNames = {
                "CON", "PRN", "AUX", "NUL",
                "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
                "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"
        };
        for (String reserved : reservedNames) {
            if (name.equalsIgnoreCase(reserved)) {
                log.error("Invalid video file name: name contains invalid characters: '{}'", reserved);
                return false;
            }
        }
        return true;
    }

    /**
     * Processes a single frame (image) from the video stream.
     * <br> The image is passed to the configured model for recognition,
     * and the results are integrated into the user's {@link StateMachine} based on the preset's model.
     *
     * @param img       a Base64-encoded string representing the image data
     * @param user      the user identifier
     * @param timestamp the timestamp or frame identifier
     * @return {@code true} if the image was processed successfully; {@code false} if the image data is empty
     * @see ImageSenderService#processImg(String, String, StateMachine, Map)
     */
    public boolean processImage(String img, String user, String timestamp) {
        if (img.isEmpty()) {
            return false;
        }

        StateMachine stateMachineByName = stateMachineService.getStateMachineByName(user);
        String modelWithConfig = configService.getUseModel(stateMachineByName.getPreset().getName());
        ImageSenderService imageSenderService = buildSenderService(modelWithConfig);
        Map<String, String> config = buildConfig(modelWithConfig);

        // Delegate image processing to the chosen service
        imageSenderService.processImg(img, timestamp, stateMachineByName, config);
        return true;
    }

    /**
     * Builds a configuration map for the selected model.
     * <br> If the model type is 'python', the host is used.
     * <br> If the model type is 'roboflow', the workspace name, workflow name, and workflow ID are used.
     *
     * @param modelWithConfig a string denoting the model type and configuration details (e.g., "python@localhost:5000")
     * @return a {@link Map} containing the relevant configuration
     * @see RoboflowWorkflowRepository
     */
    private Map<String, String> buildConfig(String modelWithConfig) {
        String[] split = modelWithConfig.split("@");
        Map<String, String> config = new HashMap<>();
        if (split[0].equalsIgnoreCase("python")) {
            config.put("host", split[1]);
        } else {
            config.put("workspace_name", split[1]);
            config.put("workflow_name", split[2]);
            config.put("workflow_id",
                    roboflowWorkflowRepository.findByWorkspaceNameAndWorkflowName(split[1], split[2])
                            .getWorkflowId());
        }
        return config;
    }

    /**
     * Selects the appropriate {@link ImageSenderService} based on the model configuration string.
     * <br> If 'python' is specified, {@link ImageSenderServiceImplOfPython} is used; otherwise, {@link ImageSenderServiceImplOfRoboflow}.
     *
     * @param modelWithConfig the configuration string (e.g., "python@localhost:5000")
     * @return the chosen {@link ImageSenderService} implementation
     * @see ImageSenderServiceImplOfPython
     * @see ImageSenderServiceImplOfRoboflow
     */
    private ImageSenderService buildSenderService(String modelWithConfig) {
        String[] split = modelWithConfig.split("@");
        return split[0].equalsIgnoreCase("python") ? imageSenderServiceImplOfPython : imageSenderServiceImplOfRoboflow;
    }

    /**
     * Retrieves the current progress (0 to 1) of video processing for the given user.
     * <br> The progress is calculated as the ratio of processed frames to total frames.
     *
     * @param user the user identifier
     * @return a double value representing the processing progress (0 = not started, 1 = completed)
     */
    public Double getProgress(String user) {
        // Determine which service is handling progress for the user
        ImageSenderService imageSenderService =
                imageSenderServiceImplOfPython.progressMap.containsKey(user)
                        ? imageSenderServiceImplOfPython
                        : imageSenderServiceImplOfRoboflow;

        long processedFrames = imageSenderService.progressMap.getOrDefault(user, new HashMap<>()).size();
        long totalFrames = imageSenderService.totleFramesMap.getOrDefault(user, 1L);
        return (double) processedFrames / totalFrames;
    }

    /**
     * Returns the previously uploaded video file for download or streaming.
     * <br> If the file does not exist, a 404 (Not Found) response is returned.
     *
     * @param user the user identifier
     * @return a {@link ResponseEntity} containing the video resource or a 404 error
     * @throws MalformedURLException if the file path cannot be converted to a valid URL
     */
    public ResponseEntity<?> getFileResponse(String user) throws MalformedURLException {
        // Determine which service has the temp file for the user
        ImageSenderService imageSenderService =
                imageSenderServiceImplOfPython.tempFiles.containsKey(user)
                        ? imageSenderServiceImplOfPython
                        : imageSenderServiceImplOfRoboflow;

        File videoFile = imageSenderService.tempFiles.get(user);
        if (!videoFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        // Create a resource from the file
        Resource videoResource = new UrlResource(videoFile.getAbsoluteFile().toURI());

        // Return the file as a response entity with appropriate headers
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + videoFile.getName() + "\"")
                .body(videoResource);
    }

    /**
     * Retrieves the {@link ImageSenderService} being used based on the preset name.
     * <br> This is a convenience method for external clients that need direct access to the underlying image-sending implementation.
     *
     * @param presetName the name of the preset
     * @return the corresponding {@link ImageSenderService} for that preset
     */
    public ImageSenderService getUseImageSender(String presetName) {
        return configService.getUseModel(presetName).startsWith("python")
                ? imageSenderServiceImplOfPython
                : imageSenderServiceImplOfRoboflow;
    }
}
