package nusri.fyp.demo.entity;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

/**
 * Implementation of the {@link SingleQuota} interface for the "offset" mode quota entry.
 * <br> Primarily includes the baseline value {@code quota}, upper/lower boundaries, and offset ratios.
 */
@Data
@JsonTypeName("offset")
public class SingleQuotaOfOffset implements SingleQuota {
    /**
     * The step or process name.
     */
    private String proc;

    /**
     * The baseline quota value.
     */
    private String quota;

    /**
     * The upper boundary.
     */
    private String upBoundary;

    /**
     * The upper offset ratio relative to the baseline value.
     */
    private String upRatio;

    /**
     * The lower boundary.
     */
    private String downBoundary;

    /**
     * The lower offset ratio relative to the baseline value.
     */
    private String downRatio;

    /**
     * No-argument constructor.
     */
    SingleQuotaOfOffset() {
    }

    /**
     * Constructor with parameters to initialize the step name and baseline quota value, calculating the upper and lower boundaries and offset ratios.
     *
     * @param proc  The step name.
     * @param quota The baseline quota value.
     */
    public SingleQuotaOfOffset(String proc, double quota) {
        this.quota = String.valueOf(quota);
        this.proc = proc;
        this.upRatio = "0.6";
        this.downRatio = "0.6";
        this.downBoundary = String.valueOf(0.4 * quota);
        this.upBoundary = String.valueOf(1.6 * quota);
    }

    /**
     * Returns the baseline quota value, which is the average.
     *
     * @return The baseline quota value.
     */
    @Override
    public String getQuota() {
        if (quota == null || quota.isEmpty() || Double.parseDouble(quota) < 0.01) {
            return String.valueOf((Double.parseDouble(upBoundary) + Double.parseDouble(downBoundary)) / 2);
        }
        return quota;
    }

    @Override
    public void setQuota(double v) {
        quota = String.valueOf(v);
    }
}