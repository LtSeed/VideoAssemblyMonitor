package nusri.fyp.demo.roboflow.data.entity;

import lombok.Getter;

/**
 * Enum representing different types of selectors used in the system.
 * <br> This enum defines various selector types that are used to specify the kind of input or output in the workflow or process.
 * <br> Each selector type corresponds to a different context in the system.
 */
@Getter
public enum SelectorType {

    /**
     * Represents an input image selector.
     * <br> This type is used to specify an image that is used as an input in the system.
     */
    INPUT_IMAGE("input_image"),

    /**
     * Represents a step output image selector.
     * <br> This type is used to refer to an image that is generated as an output from a step in the process.
     */
    STEP_OUTPUT_IMAGE("step_output_image"),

    /**
     * Represents an input parameter selector.
     * <br> This type is used to specify an input parameter for a process or step in the workflow.
     */
    INPUT_PARAMETER("input_parameter"),

    /**
     * Represents a step output selector.
     * <br> This type is used to refer to the output produced by a step in the workflow or process.
     */
    STEP_OUTPUT("step_output"),

    /**
     * Represents a generic selector type.
     * <br> This type is used for generic cases where the selector type is not specifically defined.
     */
    GENERIC("generic");

    private final String value;

    /**
     * Constructor for the SelectorType enum.
     *
     * @param value The string value associated with the selector type.
     */
    SelectorType(String value) {
        this.value = value;
    }

}
