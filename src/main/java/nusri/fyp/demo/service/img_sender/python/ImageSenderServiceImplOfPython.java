package nusri.fyp.demo.service.img_sender.python;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nusri.fyp.demo.repository.PythonServerRepository;
import nusri.fyp.demo.service.ConfigService;
import nusri.fyp.demo.service.img_sender.ImageSenderService;
import nusri.fyp.demo.state_machine.AbstractActionObservation;
import nusri.fyp.demo.state_machine.ActionObservation;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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
public class ImageSenderServiceImplOfPython extends ImageSenderService {

    /**
     * Flag to indicate whether the Python server should be used for inference.
     */
    public static final boolean USE_PYTHON = true;
    private final ObjectMapper objectMapper;

    private final Map<String, PythonServerLoadBalancer> loadBalancers;

    ImageSenderServiceImplOfPython(ConfigService configService, ObjectMapper objectMapper, PythonServerRepository pythonServerRepository) {
        super(configService);
        this.objectMapper = objectMapper;
        this.loadBalancers = new HashMap<>();
        pythonServerRepository.findAll().forEach(pythonServer -> {
            String port = pythonServer.getPort();
            String host = pythonServer.getHost();
            loadBalancers.put(host + ":" + port, new PythonServerLoadBalancer(host, port));
        });
    }

    /**
     * Synchronously sends an image frame (in OpenCV Mat format) to the backend Python server and returns the action observations.
     *
     * @param frame The image frame to be processed (OpenCV Mat format).
     * @return A list of {@link ActionObservation} representing the predictions made by the Python server.
     */
    @Override
    public List<ActionObservation> sendFrame(Mat frame, Map<String, String> config) {
        String host = config.getOrDefault("host", "http://localhost");
        String port = config.getOrDefault("port", "5000");
        PythonServerLoadBalancer loadBalancer = loadBalancers.get(host + ":" + port);
        byte[] frameBytes = matToByteArray(frame);
        return sendByteArray(frameBytes, loadBalancer);
    }

    /**
     * Synchronously sends an image (Base64 encoded) to the backend Python server and returns the action observations.
     *
     * @param frame The Base64 encoded image to be processed.
     * @return A list of {@link ActionObservation} representing the predictions made by the Python server.
     */
    @Override
    public List<ActionObservation> sendFrame(String frame, Map<String, String> config) {
        String host = config.getOrDefault("host", "http://localhost");
        String port = config.getOrDefault("port", "5000");
        PythonServerLoadBalancer loadBalancer = loadBalancers.get(host + ":" + port);
        return sendByteArray(Base64.getDecoder().decode(frame), loadBalancer);
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
    public CompletableFuture<List<AbstractActionObservation>> sendFrameAsync(Mat frame, String user, Map<String, String> config) {
        String host = config.getOrDefault("host", "http://localhost");
        String port = config.getOrDefault("port", "5000");
        PythonServerLoadBalancer loadBalancer = loadBalancers.get(host + ":" + port);
        // Create a CompletableFuture to execute the logic asynchronously
        CompletableFuture<List<AbstractActionObservation>> listCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                byte[] frameBytes = matToByteArray(frame);  // Convert frame to byte array
                return sendByteArray(frameBytes, loadBalancer).stream().map(o -> (AbstractActionObservation) o).toList();  // Send byte array and get the result
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
     * Sends an image byte array to the best Python instance for processing and returns a list of action observations.
     *
     * @param frameBytes The byte array representing the image (e.g., PNG encoded)
     * @return A list of {@link ActionObservation} representing the predictions made by the Python server
     */
    public List<ActionObservation> sendByteArray(byte[] frameBytes, PythonServerLoadBalancer balancer) {
        balancer.resetUnusedTime();
        String bestInstanceUrl = balancer.getBestInstance();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        HttpEntity<byte[]> requestEntity = new HttpEntity<>(frameBytes, headers);

        try {
            log.info("Sending image to {}", bestInstanceUrl);
            final RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(
                    bestInstanceUrl + "/process_image", requestEntity, String.class
            );
            if (response.getBody() != null) {
                return objectMapper.readValue(response.getBody().replace("'",""), ImageProcessResult.class).getActionObservations();
            }
        } catch (RestClientException e) {
            System.out.println("Error processing image on best instance: " + e.getMessage());
        } catch (JsonMappingException e) {
            log.info("Error processing image on best instance: {}", e.getMessage());
        } catch (JsonProcessingException e) {
            log.error("Error processing image on best instance: {}", e.getMessage());
        }

        return new ArrayList<>();
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
