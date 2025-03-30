package nusri.fyp.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import nusri.fyp.demo.dto.Alarm;
import nusri.fyp.demo.dto.PresetDto;
import nusri.fyp.demo.entity.*;
import nusri.fyp.demo.dto.ProgressBar;
import nusri.fyp.demo.repository.ActionRepository;
import nusri.fyp.demo.repository.ObjectRepository;
import nusri.fyp.demo.repository.PresetRepository;
import nusri.fyp.demo.repository.StateMachineLogRepository;
import nusri.fyp.demo.state_machine.Node;
import nusri.fyp.demo.state_machine.StateMachine;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service that manages state machines for different users, including starting, stopping, and retrieving state machine progress.
 * <br> It also handles logging of state machine executions, generating alarms based on node errors and timeouts, and managing system presets.
 */
@Service
@Slf4j
public class StateMachineService {

    private final Map<String, StateMachine> stateMachineMap = new HashMap<>();
    private final Map<String, Thread> processes = new HashMap<>();
    private final PresetRepository presetRepository;
    private final StateMachineLogRepository stateMachineLogRepository;
    private final ReviewService reviewService;
    private final ConfigService configService;

    /**
     * Constructor to inject dependencies such as the preset repository, state machine log repository, review service, and configuration service.
     *
     * @param presetRepository         Repository for preset configurations
     * @param stateMachineLogRepository Repository for state machine logs
     * @param reviewService           Service for replay/analytics
     * @param configService           Service for configurations
     */
    public StateMachineService(PresetRepository presetRepository, StateMachineLogRepository stateMachineLogRepository, ReviewService reviewService, ConfigService configService) {
        this.presetRepository = presetRepository;
        this.stateMachineLogRepository = stateMachineLogRepository;
        this.reviewService = reviewService;
        this.configService = configService;
    }

    /**
     * Gets the progress bar information for a specific user’s state machine.
     * <br> The progress bars are based on nodes in the state machine.
     *
     * @param name The user identifier
     * @return A list of progress bar data
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
     * Retrieves or creates a state machine for a given user.
     * <br> If the state machine for the user does not exist, a new state machine is created using the first preset.
     *
     * @param name The user identifier
     * @return The corresponding state machine
     */
    public StateMachine getStateMachineByName(String name) {
        List<Preset> all = getPresets();
        StateMachine orDefault = stateMachineMap.getOrDefault(name, new StateMachine(all.get(0)));
        stateMachineMap.put(name, orDefault);
        return orDefault;
    }

    private static final List<Preset> presetsCache = new ArrayList<>();

    /**
     * Fetches all presets in the system, caching the result to improve performance.
     *
     * @return A list of all presets
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
     * A scheduled task that clears the cached presets every 60 seconds.
     */
    @Scheduled(fixedRate = 60000)
    public void cleanPresets() {
        presetsCache.clear();
    }

    /**
     * Gets the alarm information for nodes in a user's state machine, including errors and timeouts.
     * <br> If the quota mode is "disabled", it will return an empty list.
     *
     * @param name The user identifier
     * @return A list of alarms based on node errors and timeouts
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

            double error = node.E(quotaConfig), timeout = node.D(quotaConfig);
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
     * Starts a default state machine, typically associated with the default preset.
     *
     * @param name The user identifier
     */
    public void startDefault(String name) {
        getStateMachineByName(name);
    }

    /**
     * Starts a state machine using the provided preset object.
     *
     * @param name   The user identifier
     * @param preset The preset object to use
     */
    public void start(String name, Preset preset) {
        stateMachineMap.put(name, new StateMachine(preset));
    }

    /**
     * Starts a state machine using the preset name.
     *
     * @param name   The user identifier
     * @param preset The name of the preset
     */
    public void start(String name, String preset) {
        stateMachineMap.put(name, new StateMachine(presetRepository.findPresetByName(preset).get(0)));
    }

    /**
     * Stops and removes the state machine for a given user without logging.
     *
     * @param user The user identifier
     */
    public void stopByName(String user) {
        this.stateMachineMap.remove(user);
        this.processes.remove(user);
    }

    /**
     * Stops and removes the state machine for a given user, interrupting any ongoing video processing threads.
     *
     * @param user The user identifier
     */
    public void stopStateMachine(String user) {
        stateMachineMap.remove(user);
        if (processes.containsKey(user)) {
            processes.get(user).interrupt();
            processes.remove(user);
        }
    }

    /**
     * Stops the state machine, logs the execution in the database, and clears the state machine instance.
     *
     * @param user The user identifier
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
     * Retrieves all preset names configured in the system.
     *
     * @return A list of preset names
     */
    public List<String> getAllPresets() {
        return presetRepository.findAllPresetNames();
    }

    /**
     * Retrieves all preset objects with detailed information.
     *
     * @return A list of preset data transfer objects {@link PresetDto}
     */
    public List<PresetDto> getAllPresetObjects() {
        return presetRepository.findAll().stream().map(PresetDto::new).toList();
    }

    /**
     * Add process thread for future interrupt.
     *
     * @param user key for the map.
     * @param thread the process thread.
     */
    public void addProcess(String user, Thread thread) {
        this.processes.put(user, thread);
    }
}
