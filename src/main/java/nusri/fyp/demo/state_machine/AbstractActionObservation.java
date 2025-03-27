package nusri.fyp.demo.state_machine;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nusri.fyp.demo.roboflow.data.entity.workflow.SinglePrediction;

/**
 * Abstract class representing a general action-observation entity that can include information such as probability, actions, and objects.
 * <br> This class uses Jackson annotations for polymorphic serialization and deserialization, allowing different subclasses to be mapped to the same parent class.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,  // or CLASS
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SinglePrediction.class, name = "singlePrediction"),
        @JsonSubTypes.Type(value = ActionObservation.class, name = "actionObservation")
})
public abstract class AbstractActionObservation {

    /**
     * Gets the probability/confidence value for the current observation.
     * <br> The value usually lies within the range of [0, 1], indicating the confidence level of the action being observed.
     *
     * @return The probability value of the current action observation
     */
    public abstract double getProbability();

    /**
     * Sets the probability/confidence value for the current observation.
     * <br> The value should lie within the range of [0, 1], indicating the confidence level of the action being observed.
     *
     * @param probability The probability value to be set for this action observation
     */
    public abstract void setProbability(double probability);

    /**
     * Gets the combined description of the action and object being observed.
     * <br> This could be a phrase like "open door" or "cut paper" that combines the action and the object.
     *
     * @return The string representing the action-object combination
     */
    public abstract String getActionAndObject();

    /**
     * Returns a string representation of the action-object pair related to this observation.
     * <br> This method is similar to {@link #getActionAndObject()} but may include additional or simplified formatting.
     *
     * @return A string representing the action-object pair in this observation
     */
    public abstract String s();
}
