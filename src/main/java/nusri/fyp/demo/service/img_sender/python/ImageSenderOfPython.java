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
import nusri.fyp.demo.service.img_sender.ImageSender;
import nusri.fyp.demo.service.img_sender.ImageSenderService;
import nusri.fyp.demo.state_machine.AbstractActionObservation;
import nusri.fyp.demo.state_machine.ActionObservation;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * <b>Implementation of {@link ImageSenderService} for sending image frames to a backend Python server (EOID) for inference.</b>
 * <br>
 * <p>This class provides methods to:</p>
 * <ul>
 *   <li>Send frames (OpenCV {@link Mat} or Base64-encoded) synchronously for immediate inference results.</li>
 *   <li>Send frames asynchronously via {@link CompletableFuture} for concurrent processing.</li>
 *   <li>Interrupt and cancel ongoing processes associated with a given user.</li>
 * </ul>
 * <br>
 * <p>Core logic includes:</p>
 * <ul>
 *   <li>Acquiring the "best" Python instance (least loaded) through {@link PythonServerLoadBalancer}.</li>
 *   <li>Sending frames (as PNG byte arrays) via HTTP to the chosen Python instance.</li>
 *   <li>Deserializing the JSON response into a list of {@link ActionObservation} objects.</li>
 * </ul>
 *
 * @author Liu Binghong
 * @since 1.0
 * @see PythonServerLoadBalancer
 * @see ConfigService
 * @see ImageSenderService
 * @see AbstractActionObservation
 * @see ActionObservation
 */
@Slf4j
public class ImageSenderOfPython implements ImageSender {

    /**
     * Flag indicating whether the Python server should be used for inference.
     * <br> Set this to false if you want to disable Python-based inference.
     */
    public static final boolean USE_PYTHON = true;

    private final ObjectMapper objectMapper;

    private final ImageSenderService imageSenderService;

    /**
     * A map of {@link PythonServerLoadBalancer} instances, keyed by their host+port strings.
     */
    private final Map<String, PythonServerLoadBalancer> loadBalancers;

    /**
     * Constructs this service, initializing load balancers from entries in the {@link PythonServerRepository}.
     * <br>
     * Each record in the repository provides a host/port, and a corresponding {@link PythonServerLoadBalancer} is created.
     *
     * @param imageSenderService image sender service.
     * @param objectMapper  the Jackson {@link ObjectMapper} for JSON parsing
     * @param pythonServerRepository the repository that holds Python server info (host, port, etc.)
     * @see PythonServerRepository
     */
    public ImageSenderOfPython(ImageSenderService imageSenderService,
                               ObjectMapper objectMapper,
                               PythonServerRepository pythonServerRepository) {
        this.imageSenderService = imageSenderService;
        this.objectMapper = objectMapper;
        this.loadBalancers = new HashMap<>();

        // Initialize load balancers for each server in the repository
        pythonServerRepository.findAll().forEach(pythonServer -> {
            String port = pythonServer.getPort();
            String host = pythonServer.getHost();
            loadBalancers.put(host + ":" + port, new PythonServerLoadBalancer(host, port));
        });
    }

    /**
     * Sends an OpenCV {@link Mat} frame synchronously to a Python server for inference.
     * <br> Converts the frame to a PNG byte array internally before sending.
     *
     * @param frame  the OpenCV Mat image frame
     * @param config a map containing configuration details (e.g., "host", "port")
     * @return a list of {@link ActionObservation} representing the predictions from the Python server
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
     * Sends a Base64-encoded image string synchronously to a Python server for inference.
     *
     * @param frame  a Base64-encoded image string
     * @param config a map containing configuration details (e.g., "host", "port")
     * @return a list of {@link ActionObservation} representing the predictions from the Python server
     */
    @Override
    public List<ActionObservation> sendFrame(String frame, Map<String, String> config) {
        String host = config.getOrDefault("host", "http://localhost");
        String port = config.getOrDefault("port", "5000");
        PythonServerLoadBalancer loadBalancer = loadBalancers.get(host + ":" + port);

        byte[] decodedBytes = Base64.getDecoder().decode(frame);
        return sendByteArray(decodedBytes, loadBalancer);
    }

    /**
     * Sends an OpenCV {@link Mat} frame asynchronously to a Python server for inference.
     * <br> The result is returned via a {@link CompletableFuture} of {@link AbstractActionObservation}.
     *
     * @param frame the OpenCV Mat image frame
     * @param user  the user identifier (to correlate tasks and allow interruption)
     * @param config a map containing configuration details (e.g., "host", "port")
     * @return a {@link CompletableFuture} containing a list of {@link AbstractActionObservation}
     * @see #interruptSendingProcesses(String)
     */
    @Override
    public CompletableFuture<List<AbstractActionObservation>> sendFrameAsync(Mat frame, String user, Map<String, String> config) {
        String host = config.getOrDefault("host", "http://localhost");
        String port = config.getOrDefault("port", "5000");
        PythonServerLoadBalancer loadBalancer = loadBalancers.get(host + ":" + port);

        CompletableFuture<List<AbstractActionObservation>> futureResult = CompletableFuture.supplyAsync(() -> {
            try {
                byte[] frameBytes = matToByteArray(frame);
                // Convert List<ActionObservation> -> List<AbstractActionObservation>
                return sendByteArray(frameBytes, loadBalancer).stream().map(o -> (AbstractActionObservation) o).toList();
            } catch (CompletionException | CancellationException e) {
                log.info("Task was cancelled by user");
                return new ArrayList<>();
            }
        });

        // Track this future in the map for possible interruption later
        List<CompletableFuture<List<AbstractActionObservation>>> userFutures =
                imageSenderService.sendingProcesses.getOrDefault(user, new ArrayList<>());
        userFutures.add(futureResult);
        imageSenderService.sendingProcesses.put(user, userFutures);

        return futureResult;
    }

    /**
     * Cancels all running or pending {@link CompletableFuture} tasks associated with the specified user.
     * <br> Useful for stopping video processing or image processing if the user ends their session.
     *
     * @param user the user identifier
     */
    public void interruptSendingProcesses(String user) {
        if (!imageSenderService.sendingProcesses.containsKey(user)) {
            return;
        }
        List<CompletableFuture<List<AbstractActionObservation>>> futures = imageSenderService.sendingProcesses.get(user);
        try {
            futures.forEach(future -> future.cancel(true));
        } catch (Exception ignored) {
            // Nothing special to handle; the futures are being canceled
        }
    }

    /**
     * Interrupts (cancels) the entire ongoing inference process for a specific user,
     * including removing stored frames, progress info, and any pending tasks.
     *
     * @param user the user identifier
     * @see #interruptSendingProcesses(String)
     */
    @Override
    public void interrupt(String user) {
        interruptSendingProcesses(user);
        imageSenderService.totleFramesMap.remove(user);
        imageSenderService.progressMap.remove(user);
        imageSenderService.sendingProcesses.remove(user);

        // Delete the temporary video file if present
        if (imageSenderService.tempFiles.containsKey(user)) {
            File file = imageSenderService.tempFiles.get(user);
            new Thread(() -> {
                int cnt = 0;
                while (!file.delete()) {
                    cnt++;
                    if (cnt == 5) {
                        break;
                    }
                }
                imageSenderService.tempFiles.remove(user);
            }).start();
        }
    }

    /**
     * Converts an OpenCV {@link Mat} object to a PNG-encoded byte array.
     *
     * @param mat the OpenCV Mat image
     * @return a PNG-encoded byte array
     */
    private byte[] matToByteArray(Mat mat) {
        MatOfByte buf = new MatOfByte();
        if (!Imgcodecs.imencode(".png", mat, buf)) {
            log.warn("Error occurred when converting Mat to byte array");
        }
        return buf.toArray();
    }

    /**
     * Sends a byte array (PNG-encoded image) to the selected Python server instance and processes the response into
     * {@link ActionObservation} objects.
     *
     * @param frameBytes a PNG-encoded byte array
     * @param balancer   the {@link PythonServerLoadBalancer} to select the best instance for load balancing
     * @return a list of {@link ActionObservation} returned by the Python server
     * @see PythonServerLoadBalancer#getBestInstance()
     */
    public List<ActionObservation> sendByteArray(byte[] frameBytes, PythonServerLoadBalancer balancer) {
        balancer.resetUnusedTime();
        String bestInstanceUrl = balancer.getBestInstance();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        HttpEntity<byte[]> requestEntity = new HttpEntity<>(frameBytes, headers);

        try {
            log.info("Sending image to {}", bestInstanceUrl);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(
                    bestInstanceUrl + "/process_image",
                    requestEntity,
                    String.class
            );

            if (response.getBody() != null) {
                // Clean up single quotes, if any, and parse the JSON
                String responseBody = response.getBody().replace("'", "");
                return objectMapper.readValue(responseBody, ImageProcessResult.class).getActionObservations();
            }
        } catch (RestClientException e) {
            log.warn("Error processing image on best instance: {}", e.getMessage());
        } catch (JsonMappingException e) {
            log.warn("Error mapping JSON response: {}", e.getMessage());
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON response: {}", e.getMessage());
        }

        return new ArrayList<>();
    }

    /**
     * <b>Internal helper class</b> used to parse the recognition results returned by the Python server.
     * <br> Contains a list of {@link ActionObservation} and a timestamp {@code t}.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    static class ImageProcessResult {

        /**
         * The list of recognized actions from the Python server.
         */
        @JsonProperty("actionObservations")
        private List<ActionObservation> actionObservations = new ArrayList<>();

        /**
         * A timestamp (optional), indicating when the inference occurred.
         */
        private Long t;
    }
}
