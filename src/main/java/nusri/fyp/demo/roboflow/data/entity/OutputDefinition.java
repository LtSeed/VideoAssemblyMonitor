package nusri.fyp.demo.roboflow.data.entity;

import lombok.Data;
import java.util.List;

/**
 * Represents the definition of an output.
 * <br> This class encapsulates the details of an output, including its name and the associated kind(s).
 * The kind represents the type or category of the output, providing more context on the output's nature.
 */
@Data
public class OutputDefinition {

    /**
     * The name of the output.
     * <br> This represents the label or identifier for the output, which can be used to reference the output.
     */
    private String name;

    /**
     * The list of kinds associated with this output.
     * <br> The kind represents the type or category of the output, providing further details about the nature
     * of the output, such as its format or expected values.
     * <br> This list allows for multiple types or classifications to be assigned to a single output.
     */
    private List<Kind> kind; // Replace Object with a specific type if available
}
