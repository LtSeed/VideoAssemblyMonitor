package nusri.fyp.demo.dto;

import lombok.Data;

/**
 * DTO for step statistics, including step name, average time, and standard deviation.
 */
@Data
public class StepStatsDto {
    /**
     * The name of the step.
     */
    private String stepName;

    /**
     * The average time taken for the step.
     */
    private double averageTime;

    /**
     * The standard deviation of the time taken for the step.
     */
    private double stdDev;
}