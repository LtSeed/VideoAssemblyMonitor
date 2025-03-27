package nusri.fyp.demo.controller;

import lombok.extern.slf4j.Slf4j;
import nusri.fyp.demo.dto.PresetNodeDto;
import nusri.fyp.demo.dto.StateMachineLogDto;
import nusri.fyp.demo.dto.StepStatsDto;
import nusri.fyp.demo.entity.StateMachineLog;
import nusri.fyp.demo.entity.PresetNode;
import nusri.fyp.demo.repository.ActionRepository;
import nusri.fyp.demo.repository.ObjectRepository;
import nusri.fyp.demo.repository.StateMachineLogRepository;
import nusri.fyp.demo.service.ConfigService;
import nusri.fyp.demo.service.ReviewService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controller class responsible for handling the review-related endpoints.
 * <br> This controller provides API endpoints for retrieving state machine logs, timelines,
 * filtered timelines, and step statistics for various presets.
 */
@Slf4j
@RestController
@RequestMapping("/review")
public class ReviewController {

    private final StateMachineLogRepository stateMachineLogRepository;
    private final ReviewService reviewService;
    private final ConfigService configService;
    private final ActionRepository actionRepository;
    private final ObjectRepository objectRepository;

    /**
     * Retrieves all state machine logs.
     * <br> This endpoint returns a list of all state machine logs in the system.
     * <br> The logs are returned as a list of {@link StateMachineLogDto}.
     *
     * @return A list of state machine logs in DTO format.
     */
    @GetMapping("/state-machine-logs")
    public List<StateMachineLogDto> getAllStateMachineLogs() {
        return stateMachineLogRepository.findAll().stream().map(StateMachineLogDto::new).toList();
    }

    /**
     * Retrieves the timeline for a given state machine log.
     * <br> This endpoint returns the timeline for the state machine log identified by its ID.
     * <br> The timeline is returned as a map where each key is a timestamp and the value is a {@link PresetNodeDto}.
     *
     * @param id The ID of the state machine log.
     * @return The timeline of the state machine log as a map of timestamps to {@link PresetNodeDto} objects.
     */
    @GetMapping("/state-machine-logs/{id}/timeline")
    @Cacheable(value = "getTimeline", key = "#id")
    public ResponseEntity<Map<Long, PresetNodeDto>> getTimeline(@PathVariable("id") Long id) {
        StateMachineLog smLog = reviewService.getStateMachineLog(id);
        if (smLog == null) {
            return ResponseEntity.notFound().build();
        }
        TreeMap<Long, PresetNode> timeline = smLog.getTimelineOfProc(configService, log);
        TreeMap<Long, PresetNodeDto> filteredDto = new TreeMap<>();
        if (timeline == null) {
            return ResponseEntity.ok(filteredDto);
        }
        timeline.forEach((key, value) -> filteredDto.put(key, new PresetNodeDto(value)));
        return ResponseEntity.ok(filteredDto);
    }

    /**
     * Retrieves the filtered timeline for a given state machine log.
     * <br> This endpoint returns a filtered version of the timeline where only the most significant nodes are kept.
     * <br> The timeline is filtered based on the duration of each node.
     *
     * @param id The ID of the state machine log.
     * @return The filtered timeline of the state machine log as a map of timestamps to {@link PresetNodeDto} objects.
     */
    @GetMapping("/state-machine-logs/{id}/filtered-timeline")
    @Cacheable(value = "getFilteredTimeline", key = "#id")
    public ResponseEntity<Map<Long, PresetNodeDto>> getFilteredTimeline(@PathVariable("id") Long id) {
        StateMachineLog smLog = reviewService.getStateMachineLog(id);
        if (smLog == null) {
            return ResponseEntity.notFound().build();
        }
        TreeMap<Long, PresetNode> timeline = smLog.getTimelineOfProc(configService, log);
        if (timeline == null) {
            return ResponseEntity.ok(new TreeMap<>());
        }
        TreeMap<Long, PresetNode> filtered = reviewService.filter(timeline, smLog.getDuration());
        TreeMap<Long, PresetNodeDto> filteredDto = new TreeMap<>();
        if (filtered == null) {
            return ResponseEntity.ok(filteredDto);
        }
        filtered.forEach((key, value) -> filteredDto.put(key, new PresetNodeDto(value)));
        return ResponseEntity.ok(filteredDto);
    }


    /**
     * Retrieves statistics for steps under a specific preset.
     * <br> This endpoint calculates and returns the statistics for each step, such as average duration and standard deviation.
     *
     * @param presetName The name of the preset.
     * @return A list of {@link StepStatsDto} containing the statistics for each step.
     */
    @GetMapping("/step-stats/{presetName}")
    public ResponseEntity<List<StepStatsDto>> getStepStats(@PathVariable String presetName) {
        return ResponseEntity.ok(reviewService.getStepStats(presetName));
    }

    /**
     * Retrieves statistics for all steps across all presets.
     * <br> This endpoint calculates and returns statistics for each step, such as average duration and standard deviation,
     * across all available presets.
     *
     * @return A list of {@link StepStatsDto} containing the statistics for all steps.
     */
    @GetMapping("/step-stats-all")
    public ResponseEntity<List<StepStatsDto>> getAllStepStats() {
        return ResponseEntity.ok(reviewService.getAllStepStats());
    }

    /**
     * Constructs the {@link ReviewController} with the required services and repositories.
     *
     * @param stateMachineLogRepository The repository for state machine logs.
     * @param reviewService The service for handling review operations.
     * @param configService The configuration service.
     * @param actionRepository The repository for actions.
     * @param objectRepository The repository for objects.
     */
    ReviewController(StateMachineLogRepository stateMachineLogRepository, ReviewService reviewService, ConfigService configService, ActionRepository actionRepository, ObjectRepository objectRepository) {
        this.stateMachineLogRepository = stateMachineLogRepository;
        this.reviewService = reviewService;
        this.configService = configService;
        this.actionRepository = actionRepository;
        this.objectRepository = objectRepository;
    }

}
