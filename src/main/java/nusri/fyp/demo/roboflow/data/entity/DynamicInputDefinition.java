package nusri.fyp.demo.roboflow.data.entity;

import lombok.Data;
import java.util.List;

/**
 * Represents the definition of a dynamic input.
 * <br> A dynamic input is a configurable parameter for a dynamic block, specifying how input data
 * should be provided or processed within a workflow.
 */
@Data
public class DynamicInputDefinition {

    /**
     * The type of the dynamic input.
     * <br> This field specifies the kind of input, such as a "file", "string", or another data type.
     */
    private String type;

    /**
     * Indicates whether the dynamic input has a default value.
     * <br> If true, the input will be pre-populated with a default value.
     */
    private Boolean hasDefaultValue;

    /**
     * The default value of the dynamic input.
     * <br> This value is used when the input doesn't receive a value explicitly.
     */
    private ValueType defaultValue;

    /**
     * Indicates whether the dynamic input is optional.
     * <br> If true, the input is optional and can be left empty in the workflow configuration.
     */
    private Boolean isOptional;

    /**
     * Indicates whether the dynamic input is used as a reference for dimensionality.
     * <br> If true, this input will be used to determine the dimensions of the data in the workflow.
     */
    private Boolean isDimensionalityReference;

    /**
     * The dimensionality offset for the dynamic input.
     * <br> This value is used when the input affects the dimensionality of the data in the workflow.
     */
    private Integer dimensionalityOffset;

    /**
     * A list of selector types for this input.
     * <br> These selector types define the way in which the input value can be selected or mapped.
     */
    private List<String> selectorTypes;

    /**
     * The kind of data associated with the selector for this input.
     * <br> This defines the type or category of the data that can be selected for this input.
     */
    private Kind selectorDataKind;

    /**
     * A list of value types associated with this dynamic input.
     * <br> These value types define the acceptable data types that can be used for this input.
     */
    private List<String> valueTypes;
}
