package nusri.fyp.demo.entity;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

/**
 * Implementation of the {@link SingleQuota} interface for the "confidence" mode quota entry.
 * <br> Includes average value, standard deviation, and upper/lower boundaries for evaluating if the step time is within the expected range.
 */
@Data
@JsonTypeName("conf")
public class SingleQuotaOfConf implements SingleQuota {
    /**
     * The step or process name.
     */
    private String proc;

    /**
     * The average value (baseline quota) in string format.
     */
    private String avg;

    /**
     * The standard deviation.
     */
    private String stdDev;

    /**
     * The upper boundary.
     */
    private String upBoundary;

    /**
     * The lower boundary.
     */
    private String downBoundary;

    /**
     * Returns the baseline quota value, which is the average {@link #avg}.
     *
     * @return The baseline quota value.
     */
    @Override
    public String getQuota() {
        return avg;
    }

    @Override
    public void setQuota(double v) {
        avg = Double.toString(v);
    }
}