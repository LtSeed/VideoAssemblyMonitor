package nusri.fyp.demo.roboflow.data.entity;

import lombok.Data;
import java.util.List;

/**
 * Represents the definition of a dynamic output.
 * <br> A dynamic output is a configurable output for a dynamic block that specifies how the output data
 * should be processed or structured within a workflow.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@Data
public class DynamicOutputDefinition {

    /**
     * The type of the dynamic output.
     * <br> This field specifies the kind of output, such as a "file", "string", or another data type.
     */
    private String type;

    /**
     * A list of kinds associated with this dynamic output.
     * <br> These kinds define the types or categories of data that can be used for the output.
     */
    private List<Kind> kind;
}
