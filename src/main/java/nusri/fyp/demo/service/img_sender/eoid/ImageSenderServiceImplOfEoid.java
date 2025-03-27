package nusri.fyp.demo.service.img_sender.eoid;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nusri.fyp.demo.service.ConfigService;
import nusri.fyp.demo.service.StateMachineService;
import nusri.fyp.demo.service.img_sender.ImageSenderService;
import nusri.fyp.demo.state_machine.AbstractActionObservation;
import nusri.fyp.demo.state_machine.ActionObservation;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Implementation of {@link ImageSenderService} for sending image frames to a backend Python server (EOID) for inference.
 * <br> This class encapsulates logic to send frames to the backend Python server via a load balancer service,
 * process the results, and return action observations.
 *
 */
@Slf4j
@Service
public class ImageSenderServiceImplOfEoid extends ImageSenderService {

    final PythonServerLoadBalancerService loadBalancerService;

    /**
     * Flag to indicate whether the Python server should be used for inference.
     */
    public static final boolean USE_PYTHON = true;

    /**
     * Constructor to inject necessary dependencies like the load balancer service and config service.
     *
     * @param loadBalancerService The service responsible for load balancing requests to different Python servers.
     * @param configService The configuration service used to fetch system settings.
     */
    ImageSenderServiceImplOfEoid(PythonServerLoadBalancerService loadBalancerService, ConfigService configService) {
        super(configService);
        this.loadBalancerService = loadBalancerService;
    }

    /**
     * Synchronously sends an image frame (in OpenCV Mat format) to the backend Python server and returns the action observations.
     *
     * @param frame The image frame to be processed (OpenCV Mat format).
     * @return A list of {@link ActionObservation} representing the predictions made by the Python server.
     */
    @Override
    public List<ActionObservation> sendFrame(Mat frame) {
        byte[] frameBytes = matToByteArray(frame);
        return loadBalancerService.sendByteArray(frameBytes);
    }

    /**
     * Synchronously sends an image (Base64 encoded) to the backend Python server and returns the action observations.
     *
     * @param frame The Base64 encoded image to be processed.
     * @return A list of {@link ActionObservation} representing the predictions made by the Python server.
     */
    @Override
    public List<ActionObservation> sendFrame(String frame) {
        return loadBalancerService.sendByteArray(Base64.getDecoder().decode(frame));
    }

    /**
     * Asynchronously sends an image frame to the backend Python server to obtain the recognition results.
     * <br> This method returns a {@link CompletableFuture} that will contain the action observations once the task is completed.
     *
     * @param frame The image frame to be processed (OpenCV Mat format).
     * @param user The user identifier to associate the task with a specific user.
     * @return A {@link CompletableFuture} containing a list of {@link AbstractActionObservation} representing the predictions.
     */
    @Override
    public CompletableFuture<List<AbstractActionObservation>> sendFrameAsync(Mat frame, String user) {
        // Create a CompletableFuture to execute the logic asynchronously
        CompletableFuture<List<AbstractActionObservation>> listCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                byte[] frameBytes = matToByteArray(frame);  // Convert frame to byte array
                return loadBalancerService.sendByteArray(frameBytes).stream().map(o -> (AbstractActionObservation) o).toList();  // Send byte array and get the result
            } catch (CompletionException | CancellationException c) {
                log.info("task was cancelled by user");
                return new ArrayList<>();
            }
        });
        List<CompletableFuture<List<AbstractActionObservation>>> orDefault = sendingProcesses.getOrDefault(user, new ArrayList<>());
        orDefault.add(listCompletableFuture);
        sendingProcesses.put(user, orDefault);
        return listCompletableFuture;
    }

    /**
     * Interrupts all ongoing and pending asynchronous tasks for the specified user.
     *
     * @param user The user identifier whose tasks need to be interrupted.
     */
    public void interruptSendingProcesses(String user) {
        if (!sendingProcesses.containsKey(user)) { return; }
        List<CompletableFuture<List<AbstractActionObservation>>> completableFutures = sendingProcesses.get(user);
        try {
            completableFutures.forEach(o -> o.cancel(true));
        } catch (Exception ignored) {
        }
    }

    /**
     * Interrupts the ongoing recognition process for the specified user and cleans up related data,
     * such as frame information and cached temporary files.
     *
     * @param user The user identifier whose recognition process is to be interrupted.
     */
    @Override
    public void interrupt(String user) {
        interruptSendingProcesses(user);
        totleFramesMap.remove(user);
        progressMap.remove(user);
        sendingProcesses.remove(user);
        if (tempFiles.containsKey(user)) {
            File file = tempFiles.get(user);
            new Thread(() -> {
                int cnt = 0;
                while (!file.delete()) {
                    cnt++;
                    if (cnt == 5) { break; }
                }
                tempFiles.remove(user);
            }).start();
        }
    }

    /**
     * Converts an OpenCV Mat image to a PNG-encoded byte array.
     *
     * @param mat The OpenCV Mat image to be converted.
     * @return A byte array containing the PNG-encoded image.
     */
    private byte[] matToByteArray(Mat mat) {
        MatOfByte buf = new MatOfByte();
        if (!Imgcodecs.imencode(".png", mat, buf)) {
            System.out.println("Error occurred when converting Mat to byte array");
        }

        return buf.toArray();
    }

    /**
     * Inner class used to parse the recognition results returned by the Python server.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    static class ImageProcessResult {
        @JsonProperty("actionObservations")
        List<ActionObservation> actionObservations = new ArrayList<>();
        Long t;
    }
}
