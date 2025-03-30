package nusri.fyp.demo.service.img_sender.roboflow;

import lombok.extern.slf4j.Slf4j;
import nusri.fyp.demo.roboflow.data.entity.workflow.SinglePrediction;
import nusri.fyp.demo.service.ConfigService;
import nusri.fyp.demo.service.img_sender.ImageSenderService;
import nusri.fyp.demo.state_machine.AbstractActionObservation;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the {@link ImageSenderService} for sending image frames to Roboflow for object detection and recognition.
 * <br> This service is responsible for converting video frames (in OpenCV Mat format) to Base64 strings and then sending them
 * to the Roboflow service to obtain predictions for each frame.
 * <br>
 * Key Points:
 * <ul>
 *    <li>Concurrency: The service uses a ConcurrentHashMap to manage ongoing image processing tasks for different users, allowing asynchronous task management.</li>
 *    <li>Image Encoding: The service provides methods to encode images in Base64 format (both synchronously and asynchronously) before sending them to Roboflow.</li>
 *    <li>Interrupt Handling: It supports the ability to interrupt or cancel ongoing image processing tasks for specific users, useful for stopping long-running or unwanted tasks.</li>
 * </ul>
 **/
@Slf4j
@Service
public class ImageSenderServiceImplOfRoboflow extends ImageSenderService {

    private final RoboflowService roboflowService;

    /**
     * A concurrent map that stores the ongoing image processing tasks for different users.
     * The key is the user identifier, and the value is a {@link CompletableFuture} representing the result of the image processing.
     */
    public static final Map<String, CompletableFuture<List<AbstractActionObservation>>> processes = new ConcurrentHashMap<>();

    /**
     * Constructor that injects the necessary services.
     *
     * @param roboflowService The service responsible for sending images to Roboflow for processing.
     * @param configService The configuration service that provides necessary system settings.
     */
    ImageSenderServiceImplOfRoboflow(RoboflowService roboflowService, ConfigService configService) {
        super(configService);
        this.roboflowService = roboflowService;
    }

    /**
     * Synchronously encodes an OpenCV Mat image frame to Base64 and sends it to Roboflow for predictions.
     *
     * @param frame The image frame in OpenCV Mat format to be sent to Roboflow.
     * @return A list of {@link SinglePrediction} objects representing the predictions made by the Roboflow model.
     */
    @Override
    public List<SinglePrediction> sendFrame(Mat frame, Map<String, String> config) {
        return roboflowService.sendImg(matToBase64(frame),
                config.getOrDefault("workspace_name", "tomcai"),
                config.getOrDefault("workflow_name", "detect-count-and-visualize-2"),
                config.getOrDefault("workflow_id", "KVPLmLosVn1uvCCTbCfq"));
    }

    /**
     * Synchronously sends a Base64 encoded image string to Roboflow for predictions.
     *
     * @param frame The image in Base64 format to be sent to Roboflow.
     * @return A list of {@link SinglePrediction} objects representing the predictions made by the Roboflow model.
     */
    @Override
    public List<SinglePrediction> sendFrame(String frame, Map<String, String> config) {
        return roboflowService.sendImg(frame,
                config.getOrDefault("workspace_name", "tomcai"),
                config.getOrDefault("workflow_name", "detect-count-and-visualize-2"),
                config.getOrDefault("workflow_id", "KVPLmLosVn1uvCCTbCfq"));
    }

    /**
     * Asynchronously encodes an OpenCV Mat image frame to Base64 and sends it to Roboflow for predictions.
     * <br> This method returns a {@link CompletableFuture} that will eventually contain the prediction results.
     *
     * @param frame The image frame in OpenCV Mat format to be sent to Roboflow.
     * @param user The user identifier to distinguish different video processing tasks.
     * @return A {@link CompletableFuture} that will contain a list of {@link AbstractActionObservation} objects, representing the predictions.
     */
    @Override
    public CompletableFuture<List<AbstractActionObservation>> sendFrameAsync(Mat frame, String user, Map<String, String> config) {
        CompletableFuture<List<AbstractActionObservation>> listCompletableFuture = roboflowService.sendImgAsync(matToBase64(frame),
                config.getOrDefault("workspace_name", "tomcai"),
                config.getOrDefault("workflow_name", "detect-count-and-visualize-2"),
                config.getOrDefault("workflow_id", "KVPLmLosVn1uvCCTbCfq"));
        processes.put(user, listCompletableFuture);
        return listCompletableFuture;
    }

    /**
     * Interrupts the ongoing image sending process for a specific user, effectively canceling the associated asynchronous task.
     *
     * @param user The user identifier whose image sending process is to be interrupted.
     */
    @Override
    public void interrupt(String user) {
        CompletableFuture<List<AbstractActionObservation>> listCompletableFuture = processes.get(user);
        if (listCompletableFuture != null) {
            listCompletableFuture.cancel(true);
        }
    }

    /**
     * Converts an OpenCV Mat object to a Base64 encoded string in PNG format.
     * <br> This method first encodes the Mat object as a PNG image and then converts the resulting byte array to a Base64 string.
     *
     * @param mat The OpenCV Mat object to be converted.
     * @return The Base64 encoded string representing the Mat object as a PNG image.
     */
    public static String matToBase64(Mat mat) {
        // Encode the Mat as a PNG image and store it in a byte buffer.
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", mat, buffer);

        // Convert the byte buffer to a Base64 string.
        return Base64.getEncoder().encodeToString(buffer.toArray());
    }
}
