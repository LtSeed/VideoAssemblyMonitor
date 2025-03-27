package nusri.fyp.demo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nusri.fyp.demo.entity.StateMachineLog;

import java.time.LocalDateTime;

/**
 * DTO for the state machine log, capturing details such as user, preset, start time, end time, and duration.
 */
@Slf4j
@Setter
@Getter
public class StateMachineLogDto {
    /**
     * The unique ID of the state machine log.
     */
    private Long id;

    /**
     * The user who initiated the state machine.
     */
    private String user;

    /**
     * The preset associated with the state machine log.
     * This is related to the Preset entity and stores its ID.
     */
    private PresetDto preset;

    /**
     * The start time of the state machine process.
     */
    private LocalDateTime startTime;

    /**
     * The end time of the state machine process.
     */
    private LocalDateTime endTime;

    /**
     * The duration of the state machine process, in milliseconds.
     */
    private long duration;

    /**
     * Constructs a DTO from the {@link nusri.fyp.demo.entity.StateMachineLog} entity.
     *
     * @param stateMachineLog The state machine log entity.
     */
    public StateMachineLogDto(StateMachineLog stateMachineLog) {
        this.id = stateMachineLog.getId();
        this.user = stateMachineLog.getUser();
        this.preset = new PresetDto(stateMachineLog.getPreset());
        this.startTime = stateMachineLog.getStartTime();
        this.endTime = stateMachineLog.getEndTime();
        this.duration = stateMachineLog.getDuration();
    }

    /**
     * Returns a string representation of the state machine log DTO.
     *
     * @return The string representation of the state machine log.
     */
    @Override
    public String toString() {
        return "StateMachineLogDto{" +
                "id=" + id +
                ", user='" + user + '\'' +
                ", preset=" + preset +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", duration=" + duration +
                '}';
    }
}