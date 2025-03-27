package nusri.fyp.demo.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * A generic interface for a single quota entry, supporting different modes (e.g., offset or confidence).
 * <br> It includes abstract methods to retrieve core values such as:
 * <ul>
 *   <li>{@code proc}: The step or process name.</li>
 *   <li>{@code upBoundary}/{@code downBoundary}: Boundaries for determining quota tolerance.</li>
 *   <li>{@code quota}: The base quota value.</li>
 * </ul>
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SingleQuotaOfConf.class, name = "offset"),
        @JsonSubTypes.Type(value = SingleQuotaOfOffset.class, name = "conf")
})
public interface SingleQuota {
    /**
     * Retrieves the step or process name corresponding to the current quota.
     *
     * @return The process name.
     */
    String getProc();

    /**
     * Retrieves the upper boundary value (as a string).
     *
     * @return The upper boundary.
     */
    String getUpBoundary();

    /**
     * Retrieves the lower boundary value (as a string).
     *
     * @return The lower boundary.
     */
    String getDownBoundary();

    /**
     * Retrieves the base quota value, such as 30.0 seconds or a similar unit.
     *
     * @return The base quota value.
     */
    String getQuota();

    /**
     * Sets the quota value.
     *
     * @param v The quota value to set.
     */
    void setQuota(double v);
}