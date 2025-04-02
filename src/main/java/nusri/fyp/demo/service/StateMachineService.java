package nusri.fyp.demo.service;

import lombok.extern.slf4j.Slf4j;
import nusri.fyp.demo.dto.Alarm;
import nusri.fyp.demo.dto.PresetDto;
import nusri.fyp.demo.dto.ProgressBar;
import nusri.fyp.demo.entity.Preset;
import nusri.fyp.demo.entity.PresetNode;
import nusri.fyp.demo.entity.QuotaConfig;
import nusri.fyp.demo.entity.StateMachineLog;
import nusri.fyp.demo.repository.ActionRepository;
import nusri.fyp.demo.repository.ObjectRepository;
import nusri.fyp.demo.repository.PresetRepository;
import nusri.fyp.demo.repository.StateMachineLogRepository;
import nusri.fyp.demo.roboflow.data.entity.workflow.SinglePrediction;
import nusri.fyp.demo.service.img_sender.ImageSenderService;
import nusri.fyp.demo.state_machine.AbstractActionObservation;
import nusri.fyp.demo.state_machine.Node;
import nusri.fyp.demo.state_machine.StateMachine;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <b>Service class that manages state machines for different users.</b>
 * <br> This includes starting, stopping, and retrieving progress bars and alarms for these state machines.
 * <br> It also logs the machine executions, generates alarms, and manages system presets.
 * <br>
 * <ul>
 *   <li>Uses {@link #stateMachineMap} to store state machines by user name.</li>
 *   <li>Uses {@link #processes} to store and potentially interrupt threads associated with the state machines.</li>
 *   <li>Caches the results of {@link Preset} retrieval in {@link #presetsCache} for performance.</li>
 * </ul>
 *
 * @see StateMachine
 * @see PresetRepository
 * @see Preset
 * @see StateMachineLogRepository
 * @see ReviewService
 * @see ConfigService
 */
@Service
@Slf4j
public class StateMachineService {

    /**
     * Holds user-specific {@link StateMachine} instances.
     * <br> Key: user identifier
     * <br> Value: corresponding {@link StateMachine}
     */
    private final Map<String, StateMachine> stateMachineMap = new HashMap<>();

    /**
     * Holds user-specific {@link Thread} instances for interruption (e.g., video processing).
     * <br> Key: user identifier
     * <br> Value: corresponding running {@link Thread}
     */
    private final Map<String, Thread> processes = new HashMap<>();

    private final ReviewService reviewService;
    private final ConfigService configService;
    private final VideoService videoService;
    private final ActionRepository actionRepository;
    private final ObjectRepository objectRepository;
    private final PresetRepository presetRepository;
    private final StateMachineLogRepository stateMachineLogRepository;

    /**
     * Cache of all fetched {@link Preset} objects for performance optimization.
     */
    private static final List<Preset> presetsCache = new ArrayList<>();

    /**
     * Constructor injecting required repositories and services.
     * <br> This class depends on multiple repositories and services for its internal operations.
     *
     * @param presetRepository         the repository for managing {@link Preset} entities
     * @param stateMachineLogRepository the repository for managing {@link StateMachineLog} entities
     * @param reviewService           the service responsible for replay/analytics
     * @param configService           the service managing various configurations, including {@link QuotaConfig}
     * @param videoService            the service to handle video-related operations
     * @param actionRepository        the repository for action entities
     * @param objectRepository        the repository for object entities
     * @see PresetRepository
     * @see StateMachineLogRepository
     * @see ReviewService
     * @see ConfigService
     * @see VideoService
     * @see ActionRepository
     * @see ObjectRepository
     */
    public StateMachineService(PresetRepository presetRepository,
                               StateMachineLogRepository stateMachineLogRepository,
                               ReviewService reviewService,
                               ConfigService configService,
                               VideoService videoService,
                               ActionRepository actionRepository,
                               ObjectRepository objectRepository) {
        this.presetRepository = presetRepository;
        this.stateMachineLogRepository = stateMachineLogRepository;
        this.reviewService = reviewService;
        this.configService = configService;
        this.videoService = videoService;
        this.actionRepository = actionRepository;
        this.objectRepository = objectRepository;
    }

    /**
     * Retrieves the list of {@link ProgressBar} objects for a specified user's state machine.
     * <br> The progress bars are generated from all {@link Node} objects within that user's {@link StateMachine}.
     *
     * @param name the user identifier
     * @return a list of {@link ProgressBar} objects representing the state machine's progress
     * @see ProgressBar
     * @see Node
     */
    public List<ProgressBar> getProgressBars(String name) {
        StateMachine stateMachineByName = getStateMachineByName(name);
        List<Node> nodes = new ArrayList<>(List.copyOf(stateMachineByName.getNodes()));
        nodes.add(stateMachineByName.getIdle());
        return nodes.stream()
                .filter(node -> !node.isHandlingNode())
                .map(o -> new ProgressBar(o, configService, stateMachineByName.getPreset().getName()))
                .toList();
    }

    /**
     * Retrieves or creates (if absent) a {@link StateMachine} for a given user.
     * <br> If no machine is found for that user, it will use the first available preset from {@link #getPresets()}.
     *
     * @param name the user identifier
     * @return the corresponding {@link StateMachine}
     * @see #getPresets()
     */
    public StateMachine getStateMachineByName(String name) {
        List<Preset> all = getPresets();
        StateMachine orDefault = stateMachineMap.getOrDefault(name, new StateMachine(all.get(0)));
        stateMachineMap.put(name, orDefault);
        return orDefault;
    }

    /**
     * Fetches all {@link Preset} entities in the system, using a cached result if available.
     * <br> The cache is periodically cleared by {@link #cleanPresets()}.
     *
     * @return a list of all {@link Preset} objects
     * @see #cleanPresets()
     */
    public List<Preset> getPresets() {
        if (!presetsCache.isEmpty()) {
            return presetsCache;
        } else {
            presetsCache.addAll(presetRepository.findAll());
            return presetsCache;
        }
    }

    /**
     * A scheduled task that clears the {@link #presetsCache} every 60 seconds.
     * <br> This ensures that preset data is refreshed periodically.
     */
    @Scheduled(fixedRate = 60000)
    public void cleanPresets() {
        presetsCache.clear();
    }

    /**
     * Retrieves alarms for a given user's state machine based on node errors and timeouts.
     * <br> If the quota mode is set to <i>"disabled"</i>, this method returns an empty list.
     *
     * @param name the user identifier
     * @return a list of {@link Alarm} objects indicating errors or timeouts
     * @see QuotaConfig
     * @see Alarm
     */
    public List<Alarm> getAlarms(String name) {
        StateMachine stateMachineByName = getStateMachineByName(name);
        List<Alarm> alarms = new ArrayList<>();
        List<Node> nodes = stateMachineByName.getNodes();
        String presetName = stateMachineByName.getPreset().getName();
        QuotaConfig quotaConfig = configService.getQuotaConfig(presetName);

        if (quotaConfig.getQuotaMode().equalsIgnoreCase("disabled")) {
            return new ArrayList<>();
        }

        double pHandlingError = 1, pHandlingTimeout = 1;

        for (Node node : nodes) {
            double error = node.E(quotaConfig);
            double timeout = node.D(quotaConfig);
            boolean isHandlingNode = node.isHandlingNode();

            if (!isHandlingNode) {
                pHandlingError *= 1 - error;
                pHandlingTimeout *= 1 - timeout;
                continue;
            }

            if (error != 0) {
                int percentage = (int) Math.round(error * 100);
                alarms.add(new Alarm(
                        "Error in Node " + node.getId(),
                        "Node (" + node.getName() + ") may be done in wrong order.",
                        percentage,
                        "error"
                ));
            }

            if (timeout != 0) {
                int percentage = (int) Math.round(timeout * 100);
                alarms.add(new Alarm(
                        "Timeout in Node " + node.getId(),
                        "Node " + node.getName() + " exceeded time limit.",
                        percentage,
                        "error"
                ));
            }
        }

        pHandlingTimeout = 1 - pHandlingTimeout;
        pHandlingError = 1 - pHandlingError;

        if (pHandlingError != 0) {
            alarms.add(new Alarm(
                    "Error in Handling",
                    "Handling Node may be done in wrong order.",
                    (int) Math.round(pHandlingError * 100),
                    "warning"
            ));
        }

        if (pHandlingTimeout != 0) {
            alarms.add(new Alarm(
                    "Timeout in Handling",
                    "Handling Node exceeded time limit.",
                    (int) Math.round(pHandlingTimeout * 100),
                    "warning"
            ));
        }

        return alarms;
    }

    /**
     * Retrieves the prediction data (if any) stored in the user's state machine.
     * <br> Only returns data if all stored observations are instances of {@link SinglePrediction}.
     *
     * @param name the user identifier
     * @return a map from time in milliseconds to list of {@link SinglePrediction} objects
     */
    public Map<Long, List<SinglePrediction>> getPredictions(String name) {
        Map<Long, List<AbstractActionObservation>> observations =
                getStateMachineByName(name).getObservations();

        if (observations == null) {
            return Collections.emptyMap();
        }
        boolean allSinglePredictions = observations.values().stream()
                .flatMap(List::stream)
                .allMatch(o -> o instanceof SinglePrediction);
        if (!allSinglePredictions) {
            return Collections.emptyMap();
        }
        return observations.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .map(o -> (SinglePrediction) o)
                                .collect(Collectors.toList())
                ));
    }

    /**
     * Starts a default {@link StateMachine} for a user, typically using the first available {@link Preset}.
     *
     * @param name the user identifier
     * @see #getStateMachineByName(String)
     */
    public void startDefault(String name) {
        getStateMachineByName(name);
    }

    /**
     * Starts a {@link StateMachine} for a user using a specific {@link Preset} object.
     *
     * @param name   the user identifier
     * @param preset the {@link Preset} to use
     * @see #stateMachineMap
     */
    public void start(String name, Preset preset) {
        stateMachineMap.put(name, new StateMachine(preset));
    }

    /**
     * Starts a {@link StateMachine} for a user based on the preset name.
     * <br> Fetches the {@link Preset} from the database and then initializes a new state machine.
     *
     * @param name   the user identifier
     * @param preset the name of the {@link Preset}
     * @see PresetRepository#findPresetByName(String)
     */
    public void start(String name, String preset) {
        stateMachineMap.put(name, new StateMachine(presetRepository.findPresetByName(preset).get(0)));
    }

    /**
     * Stops and removes the {@link StateMachine} for the specified user, without logging.
     * <br> Also removes the corresponding thread from {@link #processes} if it exists.
     *
     * @param user the user identifier
     * @see #stateMachineMap
     * @see #processes
     */
    public void stopByName(String user) {
        this.stateMachineMap.remove(user);
        this.processes.remove(user);
    }

    /**
     * Stops the user's {@link StateMachine} by removing it from the map and interrupting any associated thread.
     *
     * @param user the user identifier
     * @see #processes
     */
    public void stopStateMachine(String user) {
        stateMachineMap.remove(user);
        if (processes.containsKey(user)) {
            processes.get(user).interrupt();
            processes.remove(user);
        }
    }

    /**
     * Stops the user's {@link StateMachine}, logs the execution in the database as a {@link StateMachineLog},
     * and then clears the {@link StateMachine} instance.
     * <br> The logging includes timeline data and applies a filter via {@link ReviewService}.
     *
     * @param user the user identifier
     * @see StateMachineLog
     * @see #stopStateMachine(String)
     */
    public void stopAndLogStateMachine(String user) {
        StateMachineLog stateMachineLog = new StateMachineLog();
        stateMachineLog.setUser(user);
        StateMachine stateMachineByName = getStateMachineByName(user);
        stateMachineLog.setPreset(stateMachineByName.getPreset());
        stateMachineLog.setStartTime(stateMachineByName.getStartTime());
        stateMachineLog.setEndTime(LocalDateTime.now());
        stateMachineLog.setObservations(stateMachineByName.getObservations());
        stateMachineLogRepository.save(stateMachineLog);

        // Generate timeline and log
        TreeMap<Long, PresetNode> realTime = stateMachineLog.getTimelineOfProc(configService, log);
        log.debug("Observations of Proc: {}", realTime.entrySet().stream()
                .map(o -> o.getKey() + ": " + o.getValue().getId().getNumber() + ' ' + o.getValue().getName())
                .collect(Collectors.joining("\n")));

        log.debug("Filtered Observations of Proc: {}", reviewService.filter(realTime, stateMachineLog.getDuration()).entrySet().stream()
                .map(o -> o.getKey() + ": " + o.getValue().getId().getNumber() + ' ' + o.getValue().getName())
                .collect(Collectors.joining("\n")));

        stopStateMachine(user);
    }

    /**
     * Retrieves all preset names available in the system.
     *
     * @return a list of preset names
     * @see PresetRepository#findAllPresetNames()
     */
    public List<String> getAllPresets() {
        return presetRepository.findAllPresetNames();
    }

    /**
     * Retrieves detailed {@link Preset} information wrapped in {@link PresetDto}.
     *
     * @return a list of {@link PresetDto} containing preset details
     * @see PresetDto
     */
    public List<PresetDto> getAllPresetObjects() {
        return presetRepository.findAll().stream().map(PresetDto::new).toList();
    }

    /**
     * Adds a process thread associated with a user for future interrupt or management.
     *
     * @param user   the user identifier
     * @param thread the {@link Thread} to manage
     * @see #processes
     */
    public void addProcess(String user, Thread thread) {
        this.processes.put(user, thread);
    }

    /**
     * Helper method to build a {@link ResponseEntity} related to starting a {@link StateMachine} for a user.
     * <br> It stops any existing machine, interrupts any ongoing image sending, and then starts a new machine.
     *
     * @param user   the user identifier
     * @param preset the name of the preset to start
     * @return a {@link ResponseEntity} that indicates success or failure
     * @see ImageSenderService
     * @see #start(String, Preset)
     * @see #stopStateMachine(String)
     */
    public ResponseEntity<String> getStartResponse(String user, String preset) {
        stopStateMachine(user);
        ImageSenderService imageSenderService = videoService.getUseImageSender(preset);
        imageSenderService.interrupt(user);
        List<Preset> byId = presetRepository.findPresetByName(preset);
        if (!byId.isEmpty()) {
            start(user, byId.get(0));
        } else {
            return new ResponseEntity<>("Preset not found", HttpStatus.NOT_FOUND);
        }
        return null;
    }

    /**
     * Clears the current {@link StateMachine} for a user and updates its internal timeline to a specific timestamp.
     * <br> This method is often used for replaying or skipping to a certain time in a process.
     *
     * @param name      the user identifier
     * @param timestamp the timestamp (in seconds) to jump to
     * @see #getStateMachineByName(String)
     * @see StateMachine#clearAndUpdateToTime(double, ConfigService, List, List)
     */
    public void clearAndUpdateToTime(String name, double timestamp) {
        getStateMachineByName(name).clearAndUpdateToTime(
                timestamp,
                configService,
                actionRepository.getAllActions(),
                objectRepository.getAllObjects()
        );
    }
}
