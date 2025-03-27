package nusri.fyp.demo.service;

import io.micrometer.observation.Observation;
import lombok.extern.slf4j.Slf4j;
import nusri.fyp.demo.dto.StepStatsDto;
import nusri.fyp.demo.entity.Preset;
import nusri.fyp.demo.entity.PresetNode;
import nusri.fyp.demo.entity.StateMachineLog;
import nusri.fyp.demo.repository.ActionRepository;
import nusri.fyp.demo.repository.ObjectRepository;
import nusri.fyp.demo.repository.PresetRepository;
import nusri.fyp.demo.repository.StateMachineLogRepository;
import nusri.fyp.demo.state_machine.StateMachine;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A service for replay and statistical analysis, providing functionality to process, filter, and
 * gather statistics on state machine execution logs.
 * <br> This service handles operations like filtering logs, calculating step durations, and
 * generating statistics based on preset configurations.
 */
@Slf4j
@Service
public class ReviewService {


    private final StateMachineLogRepository stateMachineLogRepository;
    private final ConfigService configService;
    private final PresetRepository presetRepository;
    private final ActionRepository actionRepository;
    private final ObjectRepository objectRepository;

    /**
     * Constructs the {@link ReviewService} with the necessary repositories and services.
     *
     * @param stateMachineLogRepository The repository for state machine logs.
     * @param configService The configuration service.
     * @param presetRepository The repository for preset configurations.
     * @param actionRepository The repository for actions.
     * @param objectRepository The repository for objects.
     */
    public ReviewService(StateMachineLogRepository stateMachineLogRepository, ConfigService configService, PresetRepository presetRepository, ActionRepository actionRepository, ObjectRepository objectRepository) {
        this.stateMachineLogRepository = stateMachineLogRepository;
        this.configService = configService;
        this.presetRepository = presetRepository;
        this.actionRepository = actionRepository;
        this.objectRepository = objectRepository;
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
            TreeMap<Long, PresetNode> timeline = smLog.getTimelineOfProc(configService, log);
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
}
