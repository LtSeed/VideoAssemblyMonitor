package nusri.fyp.demo.controller;

import lombok.extern.slf4j.Slf4j;
import nusri.fyp.demo.dto.Alarm;
import nusri.fyp.demo.dto.PresetDto;
import nusri.fyp.demo.dto.ProgressBar;
import nusri.fyp.demo.entity.Preset;
import nusri.fyp.demo.repository.ActionRepository;
import nusri.fyp.demo.repository.ObjectRepository;
import nusri.fyp.demo.repository.PresetRepository;
import nusri.fyp.demo.roboflow.data.entity.workflow.SinglePrediction;
import nusri.fyp.demo.service.ConfigService;
import nusri.fyp.demo.service.img_sender.ImageSenderService;
import nusri.fyp.demo.service.StateMachineService;
import nusri.fyp.demo.service.img_sender.python.ImageSenderServiceImplOfPython;
import nusri.fyp.demo.service.img_sender.roboflow.ImageSenderServiceImplOfRoboflow;
import nusri.fyp.demo.state_machine.AbstractActionObservation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for handling work-related information, including fetching progress, predictions, alarms,
 * and managing the state machine operations such as starting, interrupting, and retrieving presets.
 * <br> This controller exposes several endpoints to interact with the state machine, fetch work progress,
 * and handle predictions and alarms for specific work sessions.
 */
@RestController
@Slf4j
public class WorkInfoController {

    private final PresetRepository presetRepository;
    private final ConfigService configService;
    private final StateMachineService stateMachineService;
    private final ImageSenderServiceImplOfPython imageSenderServiceImplOfPython;
    private final ImageSenderServiceImplOfRoboflow imageSenderServiceImplOfRoboflow;
    private final ActionRepository actionRepository;
    private final ObjectRepository objectRepository;

    /**
     * Endpoint to get the progress of a work session.
     * <br> This method returns a list of progress bars for a specific work session, updating the progress
     * based on the given timestamp.
     *
     * @param name The name of the work session or user.
     * @param timestamp The timestamp to which the progress should be updated. If it's -1, no update is performed.
     * @return A list of {@link ProgressBar} objects representing the progress of different steps.
     */
    @GetMapping("/progress")
    public List<ProgressBar> getProgress(@RequestParam String name, @RequestParam double timestamp) {
        if (timestamp != -1)
            stateMachineService.getStateMachineByName(name).clearAndUpdateToTime(timestamp, configService, actionRepository.getAllActions(), objectRepository.getAllObjects());
        return stateMachineService.getProgressBars(name);
    }

    /**
     * Endpoint to retrieve predictions for a specific work session.
     * <br> This method returns the predictions made by the model for each frame of the video in the work session.
     *
     * @param name The name of the work session or user.
     * @return A map of timestamped predictions, where the key is the timestamp and the value is a list of {@link SinglePrediction} objects.
     */
    @GetMapping("/predictions")
    public Map<Long, List<SinglePrediction>> getPredictions(@RequestParam String name) {
        Map<Long, List<AbstractActionObservation>> observations =
                stateMachineService.getStateMachineByName(name).getObservations();

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
                                .map(o -> (SinglePrediction) o) // Cast as SinglePrediction, confirmed via instanceof check
                                .collect(Collectors.toList())
                ));
    }

    /**
     * Endpoint to get the alarms associated with a specific work session.
     * <br> This method retrieves the list of alarms generated during the work session.
     *
     * @param name The name of the work session or user.
     * @return A list of {@link Alarm} objects representing alarms for the specified work session.
     */
    @GetMapping("/alarm")
    public List<Alarm> getAlarms(@RequestParam String name) {
        return stateMachineService.getAlarms(name);
    }

    /**
     * Endpoint to start a work session with a specified preset.
     * <br> This method initializes the state machine for the user, selects the preset model, and starts the session.
     * If the preset is not found, an error response is returned.
     *
     * @param user The user identifier.
     * @param preset The preset name to start the work session with.
     * @return A response indicating whether the session was successfully started or if an error occurred.
     */
    @PostMapping("/start")
    public ResponseEntity<?> getAlarms(@RequestParam String user, @RequestParam String preset) {
        stateMachineService.stopStateMachine(user);
        String s = configService.getUseModel(preset);
        ImageSenderService imageSenderService = s.equalsIgnoreCase("eoid") ? imageSenderServiceImplOfPython : imageSenderServiceImplOfRoboflow;
        imageSenderService.interrupt(user);
        List<Preset> byId = presetRepository.findPresetByName(preset);
        if (!byId.isEmpty()) {
            stateMachineService.start(user, byId.get(0));
        } else {
            return new ResponseEntity<>("Preset not found", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint to interrupt a specific work session.
     * <br> This method stops the state machine and interrupts the associated image sender service.
     *
     * @param user The user identifier for the work session to interrupt.
     */
    @GetMapping("/interrupt")
    public void interruptStateMachine(@RequestParam String user) {
        String s = configService.getUseModel(stateMachineService.getStateMachineByName(user).getPreset().getName());
        stateMachineService.stopAndLogStateMachine(user);
        ImageSenderService imageSenderService = s.equalsIgnoreCase("eoid") ? imageSenderServiceImplOfPython : imageSenderServiceImplOfRoboflow;
        imageSenderService.interrupt(user);
    }

    /**
     * Endpoint to retrieve all available presets.
     * <br> This method returns a list of preset names available in the system.
     *
     * @return A list of preset names.
     */
    @GetMapping("/all-preset")
    public ResponseEntity<List<String>> getAllPresets() {
        List<String> presets = stateMachineService.getAllPresets();
        return ResponseEntity.ok(presets);
    }

    /**
     * Endpoint to retrieve all available preset objects.
     * <br> This method returns a list of preset objects, which include more detailed information about the presets.
     *
     * @return A list of {@link PresetDto} objects representing all available presets.
     */
    @GetMapping("/all-preset-obj")
    public ResponseEntity<List<PresetDto>> getAllPresetObjects() {
        return ResponseEntity.ok(stateMachineService.getAllPresetObjects());
    }

    /**
     * Constructs a {@link WorkInfoController} with the required services and repositories.
     *
     * @param stateMachineService The state machine service.
     * @param presetRepository The preset repository.
     * @param configService The configuration service.
     * @param imageSenderServiceImplOfPython The EOID implementation of the image sender service.
     * @param imageSenderServiceImplOfRoboflow The Roboflow implementation of the image sender service.
     * @param actionRepository The action repository.
     * @param objectRepository The object repository.
     */
    WorkInfoController(StateMachineService stateMachineService,
                       PresetRepository presetRepository,
                       ConfigService configService,
                       ImageSenderServiceImplOfPython imageSenderServiceImplOfPython,
                       ImageSenderServiceImplOfRoboflow imageSenderServiceImplOfRoboflow,
                       ActionRepository actionRepository,
                       ObjectRepository objectRepository) {
        this.stateMachineService = stateMachineService;
        this.presetRepository = presetRepository;
        this.configService = configService;
        this.imageSenderServiceImplOfPython = imageSenderServiceImplOfPython;
        this.imageSenderServiceImplOfRoboflow = imageSenderServiceImplOfRoboflow;
        this.actionRepository = actionRepository;
        this.objectRepository = objectRepository;
    }
}
