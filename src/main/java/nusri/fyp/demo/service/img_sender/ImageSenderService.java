package nusri.fyp.demo.service.img_sender;

import lombok.extern.slf4j.Slf4j;
import nusri.fyp.demo.service.ConfigService;
import nusri.fyp.demo.state_machine.AbstractActionObservation;
import nusri.fyp.demo.state_machine.StateMachine;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * <b>Abstract service for sending image or video frames to a model for recognition tasks.</b>
 * <br>
 * <p>This class defines shared functionality for reading video files, extracting frames, and delegating
 * the actual recognition to specific implementations via its abstract methods. The recognized output is captured as
 * {@link AbstractActionObservation} objects.</p>
 * <br>
 * <ul>
 *   <li><b>Video Frame Extraction:</b> This class uses OpenCV's {@link VideoCapture} to read video files and
 *   split them into frames.</li>
 *   <li><b>Frame Skipping:</b> The number of frames to skip is determined by {@link ConfigService}.
 *   This helps manage performance by not processing every single frame if unnecessary.</li>
 *   <li><b>Asynchronous Processing:</b> Each frame can be sent asynchronously (implementation-defined in
 *   {@link #sendFrameAsync(Mat, String, Map)}) to allow concurrent recognition tasks.</li>
 * </ul>
 *
 * <p>The resulting maps and data structures are stored in concurrency-friendly collections like {@link ConcurrentHashMap}
 * to handle parallel processing without locking complexities.</p>
 *
 * @author Liu Binghong
 * @since 1.0
 * @see ConfigService
 * @see AbstractActionObservation
 * @see StateMachine
 * @see #sendVideoFile(File, String, Map)
 */
@Service
@Slf4j
public abstract class ImageSenderService {

    /**
     * The {@link ConfigService} providing configuration such as frame interval or video path.
     */
    private final ConfigService configService;

    /**
     * A mapping from user identifier to their temporary uploaded {@link File} reference.
     * <br> This is commonly used to store a user's uploaded video file before processing.
     */
    public final Map<String, File> tempFiles = new ConcurrentHashMap<>();

    /**
     * A mapping from user identifier to the total number of frames (after skipping) in the uploaded video.
     * <br> Used to track progress in recognition tasks.
     */
    public final Map<String, Long> totleFramesMap = new ConcurrentHashMap<>();

    /**
     * Tracks the recognition results (inference outcomes) by user.
     * <br> The outer map key is the user identifier.
     * <br> The inner map key is the frame timestamp (in milliseconds).
     * <br> The inner map value is a list of {@link AbstractActionObservation} for that frame.
     */
    public final Map<String, Map<Long, List<? extends AbstractActionObservation>>> progressMap = new ConcurrentHashMap<>();

    /**
     * A mapping from user identifier to a list of {@link CompletableFuture} tasks representing in-flight recognition processes.
     * <br> Useful for potential interruption or cancellation of ongoing tasks.
     */
    public final Map<String, List<CompletableFuture<List<AbstractActionObservation>>>> sendingProcesses = new ConcurrentHashMap<>();

    /**
     * Constructor that injects the config service.
     * <br> The config service is used for retrieving settings like the frame interval or video path.
     *
     * @param configService the configuration service object
     * @see ConfigService
     */
    public ImageSenderService(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * Synchronously sends an image frame (OpenCV {@link Mat}) to the model for recognition.
     * <br> Implementations are expected to convert the frame as needed (e.g., to a PNG byte array or a Base64 string).
     *
     * @param frame  the OpenCV {@link Mat} object representing a single image frame
     * @param config a {@link Map} of relevant configurations (e.g., host, port, or other settings)
     * @return a list of {@link AbstractActionObservation} instances representing the recognized actions/objects
     * @throws IOException if an I/O problem occurs during sending
     */
    public abstract List<? extends AbstractActionObservation> sendFrame(Mat frame, Map<String, String> config) throws IOException;

    /**
     * Synchronously sends a Base64-encoded image string to the model for recognition.
     * <br> Useful for scenarios where frames are captured or stored in Base64.
     *
     * @param frame  a Base64-encoded image string
     * @param config a {@link Map} of relevant configurations
     * @return a list of {@link AbstractActionObservation} instances for the recognized actions/objects
     */
    public abstract List<? extends AbstractActionObservation> sendFrame(String frame, Map<String, String> config);

    /**
     * Sends an image frame (OpenCV {@link Mat}) to the model asynchronously for recognition.
     * <br> Returns a {@link CompletableFuture} that completes when the recognition result is available.
     *
     * @param frame  the OpenCV {@link Mat} representing a single image frame
     * @param user   the user identifier (to track or cancel ongoing tasks)
     * @param config a {@link Map} of relevant configurations
     * @return a {@link CompletableFuture} containing a list of {@link AbstractActionObservation}
     */
    public abstract CompletableFuture<List<AbstractActionObservation>> sendFrameAsync(Mat frame, String user, Map<String, String> config);

    /**
     * Interrupts (cancels) the ongoing recognition process for the specified user, if any.
     * <br> Typically cancels any pending or running {@link CompletableFuture} tasks in {@link #sendingProcesses}.
     *
     * @param user the user identifier
     */
    public abstract void interrupt(String user);

    /**
     * Reads a video file from disk and sends each frame to the model for recognition.
     * <br> This process uses:
     * <ul>
     *     <li>OpenCV's {@link VideoCapture} to read frames from the file.</li>
     *     <li>The frame-skip interval from {@link ConfigService} to potentially skip frames.</li>
     *     <li>An asynchronous sending mechanism:
     *         see {@link #sendFrameAsync(Mat, String, Map)} for details on how frames are actually recognized.</li>
     * </ul>
     *
     * @param file  the video {@link File} to process
     * @param user  the user identifier (used for tracking progress and for cancellations)
     * @param config additional configuration parameters (e.g., host and port for the recognition service)
     * @return a {@link Map} where each key is the frame timestamp (in ms), and each value is a list of recognition results
     * @throws IOException if the video file cannot be opened or an error occurs in reading frames
     *
     * @see VideoCapture
     * @see Videoio#CAP_PROP_FRAME_COUNT
     * @see Videoio#CAP_PROP_FPS
     */
    public Map<Long, List<? extends AbstractActionObservation>> sendVideoFile(File file, String user, Map<String, String> config) throws IOException {
        File dir = new File(configService.getVideoPath());
        if (!dir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();  // Create the directory if it doesn't exist
        }

        String absolutePath = file.getAbsolutePath();
        log.info("Reading video file at: {}", absolutePath);
        VideoCapture videoCapture = new VideoCapture(absolutePath);

        if (!videoCapture.isOpened()) {
            throw new IOException("Error opening video file");
        }

        Mat frame = new Mat();
        Map<Long, List<? extends AbstractActionObservation>> observations = new ConcurrentHashMap<>();

        // Frame skipping logic
        int jump = Integer.parseInt(configService.getFrameInterval());

        // Calculate the total frames after skipping
        long totalFrames = (long) videoCapture.get(Videoio.CAP_PROP_FRAME_COUNT) / jump;
        totleFramesMap.put(user, totalFrames);
        log.info("Total frames: {}", totalFrames);

        progressMap.put(user, observations);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        int filter = 0; // Used to track skipped frames
        long frameIndex = 0;
        double fps = videoCapture.get(Videoio.CAP_PROP_FPS);
        log.info("fps: {}", fps);

        // Read and process frames
        while (videoCapture.read(frame)) {
            if (frame.empty()) {
                break; // End of video
            }
            filter++;
            if (filter % jump != 0) {
                continue;
            }
            Mat clonedFrame = new Mat();
            frame.copyTo(clonedFrame);

            CompletableFuture<List<AbstractActionObservation>> futureResult = sendFrameAsync(clonedFrame, user, config);
            long finalFrameIndex = frameIndex;

            // Insert the recognition outcome into 'observations' when ready
            CompletableFuture<Void> frameCompletion = futureResult.thenAccept(actionObs -> {
                long frameTimestamp = (long) (finalFrameIndex * 1000 * jump / fps);
                observations.put(frameTimestamp, actionObs);
            });

            futures.add(frameCompletion);
            frameIndex++;
        }

        // Cleanup resources
        videoCapture.release();
        // Wait for all recognition tasks to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return observations;
    }

    /**
     * Processes a single Base64-encoded image and updates the {@link StateMachine}'s observations accordingly.
     * <br> It:
     * <ol>
     *   <li>Sends the image synchronously via {@link #sendFrame(String, Map)}.</li>
     *   <li>Adds the resulting observations to the state machine's observation map.</li>
     *   <li>Invokes {@link StateMachine#updateStateProbability(List, double, ConfigService)} to update probabilities.</li>
     * </ol>
     *
     * @param img          a Base64-encoded image
     * @param timestamp    the timestamp or frame index (string) for identification
     * @param stateMachine the target state machine to update
     * @param config       additional configuration parameters for the sending logic
     * @see StateMachine
     * @see #sendFrame(String, Map)
     */
    public void processImg(String img,
                           String timestamp,
                           StateMachine stateMachine,
                           Map<String, String> config) {

        // Step 1: Send the frame synchronously
        List<? extends AbstractActionObservation> actionObservations = sendFrame(img, config);

        // Step 2: Retrieve or create the observations map in the state machine
        Map<Long, List<AbstractActionObservation>> existingObs = stateMachine.getObservations();

        // Step 3: Convert the typed list to AbstractActionObservation
        List<AbstractActionObservation> typedList =
                actionObservations.stream().map(o -> (AbstractActionObservation) o).toList();

        // Step 4: Use the parsed timestamp as a key
        long timeKey = (long) Double.parseDouble(timestamp);
        existingObs.put(timeKey, typedList);

        // Step 5: Update the state machine's observation and state
        stateMachine.setObservations(existingObs);
        stateMachine.updateStateProbability(typedList, Double.parseDouble(timestamp), configService);
    }
}
