package nusri.fyp.demo.service.img_sender;

import nusri.fyp.demo.service.ConfigService;
import nusri.fyp.demo.service.img_sender.python.PythonServerLoadBalancer;
import nusri.fyp.demo.state_machine.AbstractActionObservation;
import nusri.fyp.demo.state_machine.ActionObservation;
import org.opencv.core.Mat;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * This interface defines the operations for sending image frames to a model for recognition.
 * It provides both synchronous and asynchronous methods for sending image frames in different formats.
 *
 * @author Liu Binghong
 * @since 1.0
 * @see ImageSenderService
 */
public interface ImageSender {
    /**
     * Synchronously sends an image frame (OpenCV {@link Mat}) to the model for recognition.
     * <br> Implementations are expected to convert the frame as needed (e.g., to a PNG byte array or a Base64 string).
     *
     * @param frame  the OpenCV {@link Mat} object representing a single image frame
     * @param config a {@link Map} of relevant configurations (e.g., host, port, or other settings)
     * @return a list of {@link AbstractActionObservation} instances representing the recognized actions/objects
     * @throws IOException if an I/O problem occurs during sending
     */
    List<? extends AbstractActionObservation> sendFrame(Mat frame, Map<String, String> config) throws IOException;

    /**
     * Synchronously sends a Base64-encoded image string to the model for recognition.
     * <br> Useful for scenarios where frames are captured or stored in Base64.
     *
     * @param frame  a Base64-encoded image string
     * @param config a {@link Map} of relevant configurations
     * @return a list of {@link AbstractActionObservation} instances for the recognized actions/objects
     */
    List<? extends AbstractActionObservation> sendFrame(String frame, Map<String, String> config);

    /**
     * Sends an image frame (OpenCV {@link Mat}) to the model asynchronously for recognition.
     * <br> Returns a {@link CompletableFuture} that completes when the recognition result is available.
     *
     * @param frame  the OpenCV {@link Mat} representing a single image frame
     * @param user   the user identifier (to track or cancel ongoing tasks)
     * @param config a {@link Map} of relevant configurations
     * @return a {@link CompletableFuture} containing a list of {@link AbstractActionObservation}
     */
    CompletableFuture<List<AbstractActionObservation>> sendFrameAsync(Mat frame, String user, Map<String, String> config);

    /**
     * Interrupts (cancels) the ongoing recognition process for the specified user, if any.
     * <br> Typically cancels any pending or running {@link CompletableFuture} tasks in {@link ImageSenderService}.
     *
     * @param user the user identifier
     */
    void interrupt(String user);
}
