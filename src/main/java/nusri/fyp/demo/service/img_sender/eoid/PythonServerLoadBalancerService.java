package nusri.fyp.demo.service.img_sender.eoid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nusri.fyp.demo.service.ConfigService;
import nusri.fyp.demo.state_machine.ActionObservation;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import static nusri.fyp.demo.service.img_sender.eoid.ImageSenderServiceImplOfEoid.USE_PYTHON;

/**
 * Service responsible for load balancing between multiple Python server instances for image processing tasks.
 * <br> This service periodically fetches the list of available Python instances and selects the best one based on queue size.
 * It uses this information to distribute requests efficiently to the available Python instances.
 * <br> It also provides a timer-based refresh mechanism to keep the Python instance list up to date.
 */
@Slf4j
@Service
@EnableAsync // Enable asynchronous operations
public class PythonServerLoadBalancerService {

    private final ObjectMapper objectMapper;
    private final ConfigService configService;

    final boolean DEBUG = false;

    private final RestTemplate restTemplate = new RestTemplate();

    // Shared variables that need synchronized access
    private List<Instance> pythonInstances;
    private int currentGroupSize = 2;  // Initial size of the group (set to 2; will adapt if fewer instances are available)
    private int indexWithinGroup = 0;  // Index within the current group

    private final AtomicInteger unusedTime = new AtomicInteger(0);
    // Explicit lock to protect shared state
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Constructor to inject dependencies such as ObjectMapper for JSON parsing and ConfigService for fetching Python server configurations.
     *
     * @param objectMapper   The JSON mapper used for converting responses to objects
     * @param configService  The configuration service used to get Python server information
     */
    public PythonServerLoadBalancerService(ObjectMapper objectMapper, ConfigService configService){
        this.objectMapper = objectMapper;
        this.configService = configService;
        this.pythonInstances = getPythonInstances();
    }

    /**
     * Scheduled task that periodically fetches the list of available Python instances and updates the internal state.
     * <br> The task runs every 250 milliseconds to ensure the instance list is up-to-date.
     */
    @Scheduled(fixedRate = 250)
    public void fetchPythonInstancesAsync() {
        if (!USE_PYTHON) { return; }
        if (unusedTime.getAndAdd(1) > 5) {
            return;
        }
        List<Instance> instances = getPythonInstances();
        if (!instances.isEmpty()) {
            // Sort by queue size in ascending order
            instances.sort(Comparator.comparingInt(instance -> Integer.parseInt(instance.getQueueSize())));
            // Locking to modify shared state
            lock.lock();
            try {
                this.pythonInstances = instances;
                // Reset group size if fewer than 2 instances are available
                currentGroupSize = Math.min(instances.size(), 2);
                indexWithinGroup = 0;
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * Fetches the list of all Python server instances and returns information about their queue sizes and ports.
     *
     * @return A list of Python instances
     */
    public List<Instance> getPythonInstances() {
        try {
            if (DEBUG) {
                ResponseEntity<String> response = restTemplate.exchange(
                        configService.getPythonServerHost() + ':' + configService.getPythonServerMainPort() + "/instances", HttpMethod.GET, null, String.class
                );
                System.out.println(response.getBody());
            }
            ResponseEntity<InstancesResponse> response = restTemplate.exchange(
                    configService.getPythonServerHost() + ':' + configService.getPythonServerMainPort() + "/instances", HttpMethod.GET, null, InstancesResponse.class
            );

            if (response.getBody() != null) {
                return response.getBody().getInstances();
            }
        } catch (RestClientException e) {
            System.out.println("Error while fetching Python instances: " + e.getMessage());
        }

        return new ArrayList<>();
    }

    /**
     * Selects the Python server instance with the least load (smallest queue size) and returns its URL (host + port).
     *
     * @return The URL of the selected Python instance (host + port)
     */
    public String getBestInstance() {
        while (true) {
            lock.lock();
            try {
                if (!pythonInstances.isEmpty()) {
                    int effectiveGroupSize = Math.min(currentGroupSize, pythonInstances.size());
                    if (indexWithinGroup >= effectiveGroupSize) {
                        // Move to the next group if the current one is exhausted
                        currentGroupSize++;
                        indexWithinGroup = 0;
                    }
                    Instance chosen = pythonInstances.get(indexWithinGroup);
                    indexWithinGroup++;
                    return configService.getPythonServerHost() + ':' + chosen.getPort();
                }
            } finally {
                lock.unlock();
            }
            // If no available instance, retry after waiting a bit
            try {
                //noinspection BusyWait
                Thread.sleep(5);
            } catch (InterruptedException ignored) {}
        }
    }

    /**
     * Sends an image byte array to the best Python instance for processing and returns a list of action observations.
     *
     * @param frameBytes The byte array representing the image (e.g., PNG encoded)
     * @return A list of {@link ActionObservation} representing the predictions made by the Python server
     */
    public List<ActionObservation> sendByteArray(byte[] frameBytes) {
        unusedTime.set(0);
        String bestInstanceUrl = getBestInstance();

        if (bestInstanceUrl == null) {
            return new ArrayList<>();  // If no available instance, return an empty list
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        HttpEntity<byte[]> requestEntity = new HttpEntity<>(frameBytes, headers);

        try {
            log.info("Sending image to {}", bestInstanceUrl);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    bestInstanceUrl + "/process_image", requestEntity, String.class
            );
            if (response.getBody() != null) {
                return objectMapper.readValue(response.getBody().replace("'",""), ImageSenderServiceImplOfEoid.ImageProcessResult.class).getActionObservations();
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
     * Represents the response containing a list of Python instances.
     */
    @Setter
    @Getter
    public static class InstancesResponse {
        private List<Instance> instances;
    }

    /**
     * Represents a single Python server instance, including its queue size and port number.
     */
    @Setter
    @Getter
    public static class Instance {
        private String queueSize; // The queue size for the instance
        private int port;         // The port number of the instance
    }
}
