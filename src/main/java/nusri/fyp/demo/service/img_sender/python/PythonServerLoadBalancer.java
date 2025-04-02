package nusri.fyp.demo.service.img_sender.python;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import static nusri.fyp.demo.service.img_sender.python.ImageSenderServiceImplOfPython.USE_PYTHON;

/**
 * Service responsible for load balancing between multiple Python server instances for image processing tasks.
 * <br> This service periodically fetches the list of available Python instances and selects the best one based on queue size.
 * It uses this information to distribute requests efficiently to the available Python instances.
 * <br> It also provides a timer-based refresh mechanism to keep the Python instance list up to date.
 */
@Slf4j
@EnableAsync // Enable asynchronous operations
public class PythonServerLoadBalancer {


    final boolean DEBUG = false;

    private final RestTemplate restTemplate = new RestTemplate();

    // Shared variables that need synchronized access
    private List<Instance> pythonInstances;
    private int currentGroupSize = 2;  // Initial size of the group (set to 2; will adapt if fewer instances are available)
    private int indexWithinGroup = 0;  // Index within the current group

    private final AtomicInteger unusedTime = new AtomicInteger(0);
    // Explicit lock to protect shared state
    private final ReentrantLock lock = new ReentrantLock();

    private final String pythonHost, mainPort;

    public PythonServerLoadBalancer(String pythonHost, String mainPort) {
        this.pythonInstances = getPythonInstances();
        this.pythonHost = pythonHost;
        this.mainPort = mainPort;
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
                        pythonHost + ':' + mainPort + "/instances", HttpMethod.GET, null, String.class
                );
                System.out.println(response.getBody());
            }
            ResponseEntity<InstancesResponse> response = restTemplate.exchange(
                    pythonHost + ':' + mainPort + "/instances", HttpMethod.GET, null, InstancesResponse.class
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
                    return pythonHost + ':' + chosen.getPort();
                }
            } finally {
                lock.unlock();
            }
            // If no available instance, retry after waiting a bit
            try {
                // noinspection BusyWait
                Thread.sleep(5);
            } catch (InterruptedException ignored) {}
        }
    }

    public void resetUnusedTime() {
        unusedTime.set(0);
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
