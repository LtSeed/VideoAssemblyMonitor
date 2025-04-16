package nusri.fyp.demo.entity;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the {@link SingleQuota} interface for the "confidence" mode quota entry.
 * <br> Includes average value, standard deviation, and upper/lower boundaries for evaluating if the step time is within the expected range.
 */
@Slf4j
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
        log.info(upBoundary, downBoundary, avg, proc);
        if (avg == null || avg.isEmpty() || Double.parseDouble(avg) < 0.01) {
            return String.valueOf((Double.parseDouble(upBoundary) + Double.parseDouble(downBoundary)) / 2);
        }
        return avg;
    }

    @Override
    public void setQuota(double v) {
        avg = Double.toString(v);
    }
}