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

import static nusri.fyp.demo.service.img_sender.python.ImageSenderOfPython.USE_PYTHON;

/**
 * <b>Service responsible for load balancing among multiple Python server instances.</b>
 * <br>
 * This class maintains a list of Python server instances (each with its own queue size and port) and
 * periodically fetches updated information about them. It then selects the best instance based on the smallest queue
 * size for forwarding image processing tasks.
 * <p>
 * Key responsibilities:
 * <ul>
 *     <li>Periodically fetch and update available Python instances (via {@link #fetchPythonInstancesAsync()}).</li>
 *     <li>Provide a method to obtain the "best" (least-loaded) instance (via {@link #getBestInstance()}).</li>
 *     <li>Lock-based synchronization to update and read shared state safely.</li>
 *     <li>Support for an adaptive grouping mechanism to move through instances in increasing queue-size order.</li>
 * </ul>
 * <br>
 * Usage scenario:
 * <br> - This service is typically used by {@link ImageSenderOfPython} (and others) to balance
 * the load among multiple Python servers when sending images for processing.
 * <p>
 * Note:
 * <br> - The fetch interval is 250 ms, which can be adjusted based on performance needs.
 * <br> - If no instances are available, {@link #getBestInstance()} will block, retrying until an instance appears.
 *
 * @author Liu Binghong
 * @since 1.0
 * @see ImageSenderOfPython
 */
@Slf4j
@EnableAsync
public class PythonServerLoadBalancer {

    /**
     * Flag indicating whether to enable debug logging for the HTTP /instances call.
     */
    private static final boolean DEBUG = false;

    /**
     * RestTemplate for making HTTP calls to the main server that provides the Python instance list.
     */
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * The list of currently available Python instances, sorted by ascending queue size.
     * <br> Updated periodically in {@link #fetchPythonInstancesAsync()}.
     */
    private List<Instance> pythonInstances;

    /**
     * The current group size for distributing load. Defaults to 2 (meaning we group the instances in sets of 2).
     */
    private int currentGroupSize = 2;

    /**
     * The current index within the group (from 0 to {@code currentGroupSize - 1}) for selecting instances.
     */
    private int indexWithinGroup = 0;

    /**
     * The number of consecutive idle intervals (i.e., times no requests have come in).
     * <br> Used to control how often we fetch instance updates once the system becomes idle.
     */
    private final AtomicInteger unusedTime = new AtomicInteger(0);

    /**
     * A lock to protect modifications to shared state variables: {@link #pythonInstances}, {@link #currentGroupSize}, and {@link #indexWithinGroup}.
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Host of the main server where Python instances are registered (e.g., "http://127.0.0.1").
     */
    private final String pythonHost;

    /**
     * The main port of the server where the /instances endpoint is exposed (e.g., "8080").
     */
    private final String mainPort;

    /**
     * Constructs the load balancer service with an initial list of Python instances.
     * <br> The list is retrieved by calling {@link #getPythonInstances()} immediately.
     *
     * @param pythonHost The host URL (e.g., "http://127.0.0.1") where instance info is served
     * @param mainPort   The port on that host where the /instances endpoint can be accessed
     */
    public PythonServerLoadBalancer(String pythonHost, String mainPort) {
        this.pythonHost = pythonHost;
        this.mainPort = mainPort;
        this.pythonInstances = getPythonInstances();
    }

    /**
     * Scheduled method that periodically updates {@link #pythonInstances} by fetching the /instances endpoint.
     * <br> Runs every 250 ms. If the system has been idle (i.e., no requests) for more than 5 cycles, it stops fetching
     * to reduce overhead until load resumes.
     * <br>
     * Once the new list is fetched, it is sorted by ascending queue size. The group size is reset based on the new
     * available instance count (with a minimum of 2).
     */
    @Scheduled(fixedRate = 250)
    public void fetchPythonInstancesAsync() {
        if (!USE_PYTHON) {
            return;
        }
        if (unusedTime.getAndAdd(1) > 5) {
            return;
        }

        List<Instance> instances = getPythonInstances();
        if (!instances.isEmpty()) {
            // Sort instances by ascending queue size
            instances.sort(Comparator.comparingInt(instance -> Integer.parseInt(instance.getQueueSize())));

            lock.lock();
            try {
                this.pythonInstances = instances;
                currentGroupSize = Math.min(instances.size(), 2);
                indexWithinGroup = 0;
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * Retrieves the list of Python server instances from the main server's /instances endpoint.
     * <br> If an error occurs, returns an empty list.
     *
     * @return A {@link List} of {@link Instance} objects, sorted by ascending queue size.
     */
    public List<Instance> getPythonInstances() {
        try {
            if (DEBUG) {
                // Debug-level retrieval of the raw String response
                ResponseEntity<String> response = restTemplate.exchange(
                        pythonHost + ':' + mainPort + "/instances",
                        HttpMethod.GET,
                        null,
                        String.class
                );
                System.out.println(response.getBody());
            }

            // Actual typed retrieval of the InstancesResponse
            ResponseEntity<InstancesResponse> response = restTemplate.exchange(
                    pythonHost + ':' + mainPort + "/instances",
                    HttpMethod.GET,
                    null,
                    InstancesResponse.class
            );

            if (response.getBody() != null) {
                return response.getBody().getInstances();
            }
        } catch (RestClientException e) {
            log.warn("Error while fetching Python instances: {}", e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * Returns the host/port URL of the "best" Python instance for image processing.
     * <br> Selection is based on an adaptive grouping mechanism:
     * <ul>
     *   <li>We group the instances in sets of size {@link #currentGroupSize}, sorted by queue size.</li>
     *   <li>We pick the instance at {@link #indexWithinGroup} in the current group.</li>
     *   <li>Once the group is exhausted (i.e., {@code indexWithinGroup >= currentGroupSize}), we increment {@link #currentGroupSize}
     *       to move on to the next group and reset {@code indexWithinGroup} to 0.</li>
     * </ul>
     * <br>
     * If no instances are available at the moment, the method will sleep and retry until it finds one.
     *
     * @return A string of the form "{@code http://host:port}" representing the chosen Python instance
     */
    public String getBestInstance() {
        while (true) {
            lock.lock();
            try {
                if (!pythonInstances.isEmpty()) {
                    int effectiveGroupSize = Math.min(currentGroupSize, pythonInstances.size());
                    if (indexWithinGroup >= effectiveGroupSize) {
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
            // If no instances are available, wait briefly and retry
            try {
                Thread.sleep(5);
            } catch (InterruptedException ignored) {
                // Restore the interrupt status
                Thread.currentThread().interrupt();
                return null;
            }
        }
    }

    /**
     * Resets the internal unused-time counter, indicating that the system is currently active (receiving requests).
     * <br> The fetch task in {@link #fetchPythonInstancesAsync()} uses this counter to modulate how frequently it
     * attempts to refresh the instance list when the system is idle.
     */
    public void resetUnusedTime() {
        unusedTime.set(0);
    }

    /**
     * <b>Represents the response structure returned by the /instances endpoint.</b>
     * <br> Contains a list of {@link Instance} objects detailing queue sizes and ports for each Python instance.
     */
    @Setter
    @Getter
    public static class InstancesResponse {
        /**
         * A list of available Python server instances.
         */
        private List<Instance> instances;
    }

    /**
     * <b>Represents a single Python server instance.</b>
     * <br> Each instance has:
     * <ul>
     *     <li>A queue size (string representation) indicating how many tasks are queued.</li>
     *     <li>A port number where the instance is listening for requests.</li>
     * </ul>
     */
    @Setter
    @Getter
    public static class Instance {
        /**
         * Queue size for the instance, typically stored as a string (convertible to int).
         */
        private String queueSize;

        /**
         * Port number for this Python instance.
         */
        private int port;
    }
}
