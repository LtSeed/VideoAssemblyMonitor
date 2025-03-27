package nusri.fyp.demo.service.img_sender;

import lombok.extern.slf4j.Slf4j;
import nusri.fyp.demo.service.ConfigService;
import nusri.fyp.demo.state_machine.AbstractActionObservation;
import nusri.fyp.demo.state_machine.StateMachine;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract image sending service that defines common methods for sending video frames/images to the backend model for recognition.
 * <br> This class includes common logic for reading videos and processing them frame by frame. Specific send implementation is completed by subclasses.
 */
@Service
@Slf4j
public abstract class ImageSenderService {
    private final ConfigService configService;

    /**
     * A map that stores temporary files associated with each user.
     * The map's key is the user identifier, and the value is the temporary file created for that user.
     */
    public final Map<String, File> tempFiles = new ConcurrentHashMap<>();

    /**
     * A map that stores the total number of frames for each video file associated with a user.
     * The map's key is the user identifier, and the value is the total number of frames for the video.
     */
    public final Map<String, Long> totleFramesMap = new ConcurrentHashMap<>();

    /**
     * A map that tracks the processing progress for each video file per user.
     * The map's key is the user identifier, and the value is a map where keys are frame timestamps (in milliseconds),
     * and values are lists of action observations corresponding to each frame.
     */
    public final Map<String, Map<Long, List<? extends AbstractActionObservation>>> progressMap = new ConcurrentHashMap<>();

    /**
     * A map that stores ongoing image sending processes for each user.
     * The map's key is the user identifier, and the value is a list of CompletableFuture tasks that represent the sending process for each frame.
     */
    public final Map<String, List<CompletableFuture<List<AbstractActionObservation>>>> sendingProcesses = new ConcurrentHashMap<>();

    /**
     * Constructor that injects the config service.
     *
     * @param configService The configuration service object.
     */
    public ImageSenderService(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * Synchronously sends an image frame for recognition (OpenCV Mat format).
     *
     * @param frame The OpenCV Mat object representing a single image frame.
     * @return A list of action observations corresponding to this frame.
     * @throws IOException If an I/O problem occurs during sending.
     */
    public abstract List<? extends AbstractActionObservation> sendFrame(Mat frame) throws IOException;

    /**
     * Synchronously sends an image frame (Base64 string).
     *
     * @param frame The Base64 encoded image string.
     * @return A list of action observations corresponding to this frame.
     */
    public abstract List<? extends AbstractActionObservation> sendFrame(String frame);

    /**
     * Asynchronously sends an image frame for recognition.
     *
     * @param frame The OpenCV Mat object representing a single image frame.
     * @param user  The user identifier, used to distinguish different sessions or processing workflows.
     * @return A {@link CompletableFuture} containing the list of recognition results.
     */
    public abstract CompletableFuture<List<AbstractActionObservation>> sendFrameAsync(Mat frame, String user);

    /**
     * Interrupts the ongoing recognition process for the current user, including canceling asynchronous tasks.
     *
     * @param user The user identifier.
     */
    public abstract void interrupt(String user);

    /**
     * Reads the entire video file (frame by frame) and sends it to the model service. Frames can be skipped based on the frame interval from {@code configService}.
     *
     * @param file The video file to be processed.
     * @param user The user identifier.
     * @return A map where keys are frame timestamps (in milliseconds), and values are the corresponding recognition results for each frame.
     * @throws IOException If reading the file or decoding the video fails.
     */
    public Map<Long, List<? extends AbstractActionObservation>> sendVideoFile(File file, String user) throws IOException {
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

        int jump = Integer.parseInt(configService.getFrameInterval());

        long totalFrames = (long) videoCapture.get(Videoio.CAP_PROP_FRAME_COUNT) / jump;
        totleFramesMap.put(user, totalFrames);
        log.info("Total frames: {}", totalFrames);
        progressMap.put(user, observations);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        int filter = 0;
        long frameIndex = 0;
        double fps = videoCapture.get(Videoio.CAP_PROP_FPS);
        log.info("fps: {}", fps);

        while (videoCapture.read(frame)) {
            if (frame.empty()) {
                break; // End of video
            }
            filter++;
            if (filter % jump != 0) {
                continue;
            }
            Mat m = new Mat();
            frame.copyTo(m);

            CompletableFuture<List<AbstractActionObservation>> listCompletableFuture = sendFrameAsync(m, user);

            long finalFrameIndex = frameIndex;
            CompletableFuture<Void> future;
            try {
                future = listCompletableFuture.thenAccept(f -> {
                    long frameTimestamp = (long) (finalFrameIndex * 1000 * jump / fps);
                    observations.put(frameTimestamp, f);
                });
            } catch (CompletionException | CancellationException e) {
                log.info("Task was cancelled");
                return new HashMap<>();
            }

            futures.add(future);
            frameIndex++;
        }
        videoCapture.release(); // Release the VideoCapture resource
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return observations;
    }

    /**
     * Processes and sends the binary data of a specified video (frame by frame) to the model service.
     *
     * @param videoData A byte array containing the full video content.
     * @return A map where the keys are lists of action observations, and the values are frame timestamps.
     * @throws IOException If video decoding or sending fails.
     */
    public Map<List<? extends AbstractActionObservation>, Long> processVideoFrames(byte[] videoData) throws IOException {
        File tempFile = File.createTempFile("video_", ".mp4");
        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(tempFile))) {
            stream.write(videoData);
        }

        VideoCapture videoCapture = new VideoCapture(tempFile.getAbsolutePath());

        if (!videoCapture.isOpened()) {
            throw new IOException("Error opening video stream");
        }

        Mat frame = new Mat();
        Map<List<? extends AbstractActionObservation>, Long> observations = new HashMap<>();

        while (videoCapture.read(frame)) {
            if (frame.empty()) {
                break; // End of video
            }
            observations.put(sendFrame(frame), System.currentTimeMillis());  // Send each frame for processing
        }

        videoCapture.release(); // Release the VideoCapture resource
        Files.delete(tempFile.toPath());

        return observations;
    }

    /**
     * Processes a single image and updates the recognition results in the specified state machine.
     *
     * @param img          The Base64 encoded image.
     * @param timestamp    The timestamp or frame identifier.
     * @param stateMachine The specified state machine.
     */
    public void processImg(String img, String timestamp, StateMachine stateMachine) {
        List<? extends AbstractActionObservation> actionObservations = sendFrame(img);
        Map<Long, List<AbstractActionObservation>> observations = stateMachine.getObservations();

        List<AbstractActionObservation> list = actionObservations.stream().map(o -> (AbstractActionObservation) o).toList();
        observations.put((long) Double.parseDouble(timestamp), list);

        stateMachine.setObservations(observations);
        stateMachine.updateStateProbability(list, Double.parseDouble(timestamp), configService);
    }
}
