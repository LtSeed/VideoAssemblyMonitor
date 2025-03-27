package nusri.fyp.demo.service;


import lombok.extern.slf4j.Slf4j;
import nusri.fyp.demo.service.img_sender.ImageSenderService;
import nusri.fyp.demo.service.img_sender.eoid.ImageSenderServiceImplOfEoid;
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
 * Service class for handling video uploads, processing video frames, and interacting with the state machine.
 * <br> This service processes uploaded video files, sending frames to a model for recognition and interacting with the state machine for processing.
 */
@Service
@Slf4j
public class VideoService {

    private final ConfigService configService;
    private final ImageSenderServiceImplOfEoid imageSenderServiceImplOfEoid;
    private final ImageSenderServiceImplOfRoboflow imageSenderServiceImplOfRoboflow;
    private final StateMachineService stateMachineService;

    /**
     * Constructs the {@link VideoService} with the required services for video processing.
     *
     * @param configService The configuration service.
     * @param imageSenderServiceImplOfEoid The EOID implementation of the image sender service.
     * @param imageSenderServiceImplOfRoboflow The Roboflow implementation of the image sender service.
     * @param stateMachineService The state machine service.
     */
    public VideoService(ConfigService configService,
                        ImageSenderServiceImplOfEoid imageSenderServiceImplOfEoid,
                        ImageSenderServiceImplOfRoboflow imageSenderServiceImplOfRoboflow,
                        StateMachineService stateMachineService) {
        this.configService = configService;
        this.imageSenderServiceImplOfEoid = imageSenderServiceImplOfEoid;
        this.imageSenderServiceImplOfRoboflow = imageSenderServiceImplOfRoboflow;
        this.stateMachineService = stateMachineService;
    }

    /**
     * Processes an uploaded video by sending frames to the model for recognition and storing the results in the state machine.
     * <br> The processing is done asynchronously. If no file is found for the user, an error is returned. Once processing is complete,
     * the recognition results are returned.
     *
     * @param user The user identifier, used to distinguish different users' sessions or uploads.
     * @param presetName The preset name, related to the state machine preset.
     * @return A response entity containing the result of the video processing, or an error message if an issue occurs.
     */
    public ResponseEntity<? extends Serializable> processVideo(String user, String presetName) {
        log.info("user start to process video: {}", user);
        String s = configService.getUseModel(presetName);
        ImageSenderService imageSenderService = s.equalsIgnoreCase("eoid") ? imageSenderServiceImplOfEoid : imageSenderServiceImplOfRoboflow;
        if (!imageSenderService.tempFiles.containsKey(user)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No file uploaded");
        }
        Date start = new Date();
        // Process the video frame by frame using sendFrame method
        AtomicReference<Map<Long, List<AbstractActionObservation>>> observations = new AtomicReference<>();

        Thread thread = new Thread(() -> {
            try {
                Map<Long, List<? extends AbstractActionObservation>> longListMap = imageSenderService
                        .sendVideoFile(imageSenderService.tempFiles.get(user), user);

                Map<Long, List<AbstractActionObservation>> listMap = new TreeMap<>();

                longListMap.forEach((key, value) -> listMap.put(key, value
                        .stream()
                        .map(a -> (AbstractActionObservation) a)
                        .toList()));

                observations.set(listMap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        log.info("user start to process video- thread.start();: {}", user);
        thread.start();
        stateMachineService.addProcess(user, thread);

        try {
            thread.join();
            // Return the observations from the Python server
            if (observations.get().isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing video");
            }
            stateMachineService.start(user, presetName);
            stateMachineService.getStateMachineByName(user).setObservations(observations.get());
            log.info("user start to process video- return ResponseEntity.ok(observations);: {}", user);
            return ResponseEntity.ok(observations);
        } catch (InterruptedException | NullPointerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Interrupted by user");
        }
    }

    /**
     * Uploads and saves a video file to the server and resets the state machine.
     * <br> This method ensures that the uploaded file is stored in a fixed path, and the associated state machine is reset.
     *
     * @param videoFile The uploaded video file.
     * @param user The user identifier.
     * @return A response entity indicating the result of the save operation.
     * @throws IOException If an I/O error occurs while saving or writing the file.
     */
    public ResponseEntity<String> uploadAndSave(MultipartFile videoFile, String user) throws IOException {
        stateMachineService.stopStateMachine(user);
        imageSenderServiceImplOfEoid.interrupt(user);
        imageSenderServiceImplOfRoboflow.interrupt(user);

        if (videoFile.isEmpty()) {
            log.error("video file is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No file uploaded");
        }

        // Define the destination file path (e.g., fixed file name or timestamped file)
        String destinationPath = configService.getVideoPath() + File.separator + user + ".mp4";  // Fixed filename
        if (!isValidFilename(user)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid video file name");
        }

        // Save the file to disk
        File destinationFile = new File(destinationPath);
        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(destinationFile))) {
            stream.write(videoFile.getBytes());
            stream.flush();
        }

        // Log the file saving process
        System.out.println("Saved video file to " + destinationFile.getAbsolutePath());

        imageSenderServiceImplOfEoid.tempFiles.put(user, destinationFile);
        imageSenderServiceImplOfRoboflow.tempFiles.put(user, destinationFile);
        return null;
    }

    /**
     * Validates the given filename to ensure it does not contain invalid characters or reserved names.
     * <br> This validation is especially relevant for systems like Windows that have restrictions on file names.
     *
     * @param name The filename to validate.
     * @return {@code true} if the filename is valid; {@code false} if it contains invalid characters or reserved names.
     */
    public static boolean isValidFilename(String name) {
        if (name == null || name.trim().isEmpty()) {
            log.error("Invalid video file name: name is null or empty");
            return false;
        }

        // Check for invalid characters in the filename
        String invalidChars = "[\\\\/:*?\"<>|]";
        if (name.matches(".*" + invalidChars + ".*")) {
            log.error("Invalid video file name: name contains invalid characters");
            return false;
        }

        // Check for Windows reserved names
        String[] reservedNames = {
                "CON", "PRN", "AUX", "NUL",
                "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
                "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"
        };
        for (String reserved : reservedNames) {
            if (name.equalsIgnoreCase(reserved)) {
                log.error("Invalid video file name: name contains invalid characters: '" + reserved + "'");
                return false;
            }
        }

        return true;
    }

    /**
     * Processes a single frame (image) from the video stream, passing it to the corresponding model for recognition
     * and updating the state machine with the results.
     * <br> This method is used to process individual images, sending them to the model and updating the state machine.
     *
     * @param img The Base64 encoded image data.
     * @param user The user identifier.
     * @param timestamp The timestamp or frame identifier.
     * @return {@code true} if the image was processed successfully; {@code false} if no image was uploaded.
     */
    public boolean processImage(String img, String user, String timestamp) {
        if (img.isEmpty()) {
            return false;
        }

        StateMachine stateMachineByName = stateMachineService.getStateMachineByName(user);
        String model = configService.getUseModel(stateMachineByName.getPreset().getName());
        ImageSenderService imageSenderService = model.equalsIgnoreCase("eoid") ? imageSenderServiceImplOfEoid : imageSenderServiceImplOfRoboflow;

        imageSenderService.processImg(img, timestamp, stateMachineByName);
        return true;
    }

    /**
     * Retrieves the current video processing progress (from 0 to 1).
     * <br> This method returns the progress of the video processing, calculated as the ratio of processed frames to total frames.
     *
     * @param user The user identifier.
     * @return A double value representing the progress of the video processing (0 = not started, 1 = completed).
     */
    public Double getProgress(String user) {
        ImageSenderService imageSenderService = imageSenderServiceImplOfEoid.progressMap.containsKey(user) ? imageSenderServiceImplOfEoid : imageSenderServiceImplOfRoboflow;
        return (double) imageSenderService.progressMap.getOrDefault(user, new HashMap<>()).size() / imageSenderService.totleFramesMap.getOrDefault(user, 1L);
    }

    /**
     * Returns the previously uploaded video file for download or streaming.
     * <br> If the video file does not exist, a 404 status is returned.
     *
     * @param user The user identifier.
     * @return A response entity containing the video resource, or a 404 error if the file is not found.
     * @throws MalformedURLException If the URL format is invalid.
     */
    public ResponseEntity<?> getFileResponse(String user) throws MalformedURLException {
        ImageSenderService imageSenderService = imageSenderServiceImplOfEoid.tempFiles.containsKey(user) ? imageSenderServiceImplOfEoid : imageSenderServiceImplOfRoboflow;
        File videoFile = imageSenderService.tempFiles.get(user);
        if (!videoFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        // Return the file as a resource
        Resource videoResource = new UrlResource(videoFile.getAbsoluteFile().toURI());

        // Set the response headers for the video file (e.g., Content-Disposition for download)
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + videoFile.getName() + "\"")
                .body(videoResource);
    }
}
