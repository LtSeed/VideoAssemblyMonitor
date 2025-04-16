package nusri.fyp.demo.service;

import lombok.extern.slf4j.Slf4j;
import nusri.fyp.demo.dto.StateMachineLogDto;
import nusri.fyp.demo.dto.StepStatsDto;
import nusri.fyp.demo.entity.PresetNode;
import nusri.fyp.demo.entity.QuotaConfig;
import nusri.fyp.demo.entity.StateMachineLog;
import nusri.fyp.demo.repository.PresetRepository;
import nusri.fyp.demo.repository.StateMachineLogRepository;
import nusri.fyp.demo.state_machine.AbstractActionObservation;
import nusri.fyp.demo.state_machine.SegmentPartitionByDP;
import nusri.fyp.demo.state_machine.StateMachine;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A service for replay and statistical analysis, providing functionality to process, filter, and
 * gather statistics on state machine execution logs.
 * <br> This service handles operations like filtering logs, calculating step durations, and
 * generating statistics based on preset configurations.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@Slf4j
@Service
public class ReviewService {


    private final StateMachineLogRepository stateMachineLogRepository;
    private final ConfigService configService;
    private final PresetRepository presetRepository;


    /**
     * Constructs the {@link ReviewService} with the necessary repositories and services.
     *
     * @param stateMachineLogRepository The repository for state machine logs.
     * @param configService The configuration service.
     * @param presetRepository The repository for preset configurations.

     */
    public ReviewService(StateMachineLogRepository stateMachineLogRepository, ConfigService configService, PresetRepository presetRepository) {
        this.stateMachineLogRepository = stateMachineLogRepository;
        this.configService = configService;
        this.presetRepository = presetRepository;

    }

    /**
     * Retrieves the duration between the start time of a task and the next task's start time.
     * <br>This method is used internally for statistics.
     *
     * @param presetNodes The original timeline, where the key is the timestamp, and the value is the preset node.
     * @param key The start time of the task.
     * @param endTime The end time of the process.
     * @return The duration between the start time and the next task's start time.
     */
    private long getTime(TreeMap<Long, PresetNode> presetNodes, long key, long endTime) {
        long l = (presetNodes.higherKey(key) == null ? endTime : presetNodes.higherKey(key)) - key;
        if (l < 0) {
            l += presetNodes.firstKey();
        }

        return l;
    }

    /**
     * Filters the given timeline data, retaining the most significant nodes based on their duration and balancing
     * the time intervals.
     * <br>This method ensures that only the most significant steps, based on their duration, are kept.
     *
     * @param presetNodes The original timeline data, with timestamps as keys and preset nodes as values.
     * @param duration The total duration of the entire process.
     * @return The filtered timeline, with only the most significant steps.
     */
    public TreeMap<Long, PresetNode> filter(TreeMap<Long, PresetNode> presetNodes, long duration) {
        if (presetNodes == null) { return null; }
        Map<Integer, Long> maxTimeForState = new HashMap<>();
        for (Map.Entry<Long, PresetNode> entry : presetNodes.entrySet()) {
            int stateNum = entry.getValue().getId().getNumber();
            Long key = entry.getKey();
            long time = getTime(presetNodes, key, duration);
            maxTimeForState.put(stateNum, Math.max(maxTimeForState.getOrDefault(stateNum, 0L), time));
        }

        TreeMap<Long, PresetNode> filtered = new TreeMap<>();
        for (Map.Entry<Long, PresetNode> entry : presetNodes.entrySet()) {
            Long key = entry.getKey();
            long time = getTime(presetNodes, key, duration);
            int stateNum = entry.getValue().getId().getNumber();
            if (time != maxTimeForState.get(stateNum)) {
                continue;
            }
            filtered.put(time, entry.getValue());
        }

        long overallStart = 0;

        List<Long> keys = new ArrayList<>(filtered.keySet());
        TreeMap<Long, PresetNode> adjusted = new TreeMap<>();

        for (int i = 0; i < keys.size(); i++) {
            long leftBoundary;
            if (i == 0) {
                leftBoundary = overallStart;
            } else if (i == keys.size() - 1) {
                leftBoundary = (keys.get(i));
            } else {
                leftBoundary = (keys.get(i));
            }

            adjusted.put(leftBoundary, filtered.get(keys.get(i)));
        }

        return adjusted;
    }

    /**
     * Retrieves statistics for each step under the specified preset, such as average time and standard deviation.
     * <br>This method processes the logs and calculates step-wise statistics based on the timestamps.
     *
     * @param presetName The name of the preset.
     * @return A list of {@link StepStatsDto} containing the statistics for each step.
     */
    public List<StepStatsDto> getStepStats(String presetName) {
        Map<String, List<Long>> stepDurations = new HashMap<>();

        List<StateMachineLog> logs = getAllByPresetName(presetName);
        for (StateMachineLog smLog : logs) {
            TreeMap<Long, PresetNode> timeline = getTimelineOfProc(smLog);
            if (timeline == null) {
                continue;
            }

            for (Map.Entry<Long, PresetNode> entry : timeline.entrySet()) {
                PresetNode node = entry.getValue();
                String stepName = node.getName();
                long stepDuration = getTime(timeline, entry.getKey(), smLog.getDuration()); // 比如这样取到时长

                stepDurations
                        .computeIfAbsent(stepName, k -> new ArrayList<>())
                        .add(stepDuration);
            }
        }

        List<StepStatsDto> results = new ArrayList<>();
        for (Map.Entry<String, List<Long>> entry : stepDurations.entrySet()) {
            StepStatsDto stepStatsDto = new StepStatsDto();
            stepStatsDto.setStepName(entry.getKey());
            List<Long> durations = entry.getValue();
            if (durations.isEmpty()) {
                continue;
            }
            double avg = average(durations);
            stepStatsDto.setAverageTime(avg);
            double std = stdDev(durations, avg);
            stepStatsDto.setStdDev(std);
            results.add(stepStatsDto);
        }

        return results;
    }

    /**
     *
     * Simulates the execution of the state machine based on the observations in the current log entry.<br>
     * If Quota is enabled, it performs real-time updates; if Quota is disabled, it performs {@link SegmentPartitionByDP} inference.<br>
     * The final sequence of steps (nodes) is returned as a timeline.
     *
     * @param stateMachineLog The state machine log.
     * @return A sorted map of timestamps to the most probable {@link PresetNode} at that time.
     * @see StateMachine
     * @see SegmentPartitionByDP
     */
    public TreeMap<Long, PresetNode> getTimelineOfProc(StateMachineLog stateMachineLog) {
        log.debug("getMatchingPresetNodes: {}", stateMachineLog.getMatchingPresetNodes());

        // Initialize state machine
        StateMachine fsm = new StateMachine(stateMachineLog.getPreset());
        TreeMap<Long, List<AbstractActionObservation>> obsList = new TreeMap<>(stateMachineLog.getObservations());
        fsm.setObservations(obsList);

        // Check if Quota is enabled
        String quotaMode = configService.getQuotaConfig(stateMachineLog.getPreset().getName()).getQuotaMode();
        boolean isQuotaDisabled = "disabled".equalsIgnoreCase(quotaMode);

        TreeMap<Long, PresetNode> timeline = new TreeMap<>();

        if (!isQuotaDisabled) {
            // If Quota is enabled, update the state step by step
            for (Map.Entry<Long, List<AbstractActionObservation>> e : obsList.entrySet()) {
                Long timestampMs = e.getKey();
                double timestampSec = timestampMs.doubleValue();

                // Call the update method for each observation
                fsm.updateStateProbability(e.getValue(), timestampSec, configService);

                // Get the most probable state (PresetNode)
                PresetNode mostProbable = fsm.getMostProbableState();
                timeline.put(e.getKey(), mostProbable);
            }

        } else {
            log.debug("Quota disabled -> Using offline HMM/Viterbi inference.");
            Map<Long, List<Integer>> matchingPresetNodesIds = stateMachineLog.getMatchingPresetNodesIds(false);
            List<Long> optimalPartitions = SegmentPartitionByDP.findOptimalPartitions(matchingPresetNodesIds);

            TreeMap<Long, PresetNode> result = new TreeMap<>();

            assert optimalPartitions.size() == stateMachineLog.getPreset().getNodes().size();
            PresetNode node = null;
            try {
                node = stateMachineLog.getPreset().getNode(1);
            } catch (Throwable e) {
                log.error(e.getMessage());
            }
            if (node != null)
                result.put(stateMachineLog.getStartTime().toInstant(ZoneOffset.of("+8")).toEpochMilli(), node);

            for (int i = 1; i < optimalPartitions.size(); i++) {
                Long timestampMs = optimalPartitions.get(i);
                try {
                    node = stateMachineLog.getPreset().getNode(i + 1);
                    if (node != null)
                        result.put(timestampMs + stateMachineLog.getStartTime().toInstant(ZoneOffset.of("+8")).toEpochMilli(), node);
                } catch (Throwable e) {
                    log.error(e.getMessage());
                }
            }
            log.debug("Timeline result: {}", result);
            return result;
        }
        log.debug("Timeline result before filter: {}", timeline);

        // Filter out repeated states, only keeping the state change moments
        Map.Entry<Long, PresetNode> first = timeline.entrySet().stream()
                .filter(e -> e.getValue().getId().getNumber() >= 0)
                .min(Comparator.comparingDouble(e -> e.getValue().getId().getNumber()))
                .orElse(null);

        if (first == null) {
            return null;
        }

        Map.Entry<Long, PresetNode> longPresetNodeEntry = timeline.firstEntry();
        timeline.put(longPresetNodeEntry.getKey(), first.getValue());

        TreeMap<Long, PresetNode> filtered = new TreeMap<>();
        for (Map.Entry<Long, PresetNode> entry : timeline.entrySet()) {
            Long currentTime = entry.getKey();
            Long prevKey = timeline.lowerKey(currentTime);
            if (prevKey == null) {
                filtered.put(currentTime, entry.getValue());
                continue;
            }
            if (entry.getValue().getId().getNumber() == timeline.get(prevKey).getId().getNumber()) {
                continue;
            }
            filtered.put(currentTime, entry.getValue());
        }

        return filtered;
    }


    /**
     * Retrieves all state machine logs for the given preset name.
     * <br> This method is used internally for statistics calculations.
     *
     * @param presetName The name of the preset.
     * @return A list of {@link StateMachineLog} entries for the given preset.
     */
    private List<StateMachineLog> getAllByPresetName(String presetName) {
        return stateMachineLogRepository.findAllByPreset_Name(presetName);
    }

    /**
     * Retrieves a state machine log by its unique identifier.
     *
     * @param id The unique identifier of the state machine log.
     * @return The {@link StateMachineLog} associated with the given ID, or {@code null} if not found.
     */
    @Transactional(readOnly = true)
    public StateMachineLog getStateMachineLog(Long id) {
        return stateMachineLogRepository.findById(id).orElse(null);
    }

    /**
     * Calculates the average of a list of values.
     * <br>This method is used internally to calculate the mean of step durations.
     *
     * @param values The list of values to calculate the average.
     * @return The average value of the list.
     */
    private double average(List<Long> values) {
        double sum = 0.0;
        for (Long v : values) {
            sum += v;
        }
        return sum / values.size();
    }

    /**
     * Calculates the standard deviation for a list of values.
     * <br>This method is used internally to calculate the standard deviation of step durations.
     *
     * @param values The list of values to calculate the standard deviation.
     * @param avg The average value of the list.
     * @return The standard deviation of the values.
     */
    private double stdDev(List<Long> values, double avg) {
        if (values.size() <= 1) {
            return 0.0;
        }
        double sumSquare = 0.0;
        for (Long v : values) {
            double diff = v - avg;
            sumSquare += diff * diff;
        }
        return Math.sqrt(sumSquare / (values.size() - 1));
    }

    /**
     * Retrieves all step statistics for all presets in the system, and combines them into a single list.
     * <br>This method aggregates the statistics for all presets and returns them as a combined list of {@link StepStatsDto}.
     *
     * @return A combined list of step statistics for all presets.
     */
    public List<StepStatsDto> getAllStepStats() {
        return presetRepository.findAll()
                .stream()
                .map(preset -> getStepStats(preset.getName())
                        .stream()
                        .peek(o -> o.setStepName(preset.getName() + "." + o.getStepName()))
                        .toList())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all State Machine Logs in the database, using the {@link StateMachineLogRepository}
     *
     * @param quotaConfigs All quota configs.
     * @return A list of all State Machine Logs in the database
     */
    public List<StateMachineLogDto> getAllStateMachineLogs(Map<String, QuotaConfig> quotaConfigs) {
        return stateMachineLogRepository.findAll().stream().map(o-> new StateMachineLogDto(o, quotaConfigs.getOrDefault(o.getPreset().getName(), quotaConfigs.get("default")))).toList();
    }
}
