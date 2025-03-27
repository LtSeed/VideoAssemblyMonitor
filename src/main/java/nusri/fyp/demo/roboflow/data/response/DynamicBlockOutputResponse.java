package nusri.fyp.demo.roboflow.data.response;

import lombok.Data;
import nusri.fyp.demo.roboflow.data.RoboflowResponseData;
import nusri.fyp.demo.roboflow.data.entity.Kind;

import java.util.List;

/**
 * Represents the response for dynamic block output in the Roboflow API.
 * <br> This class contains the data returned by the API for a dynamic block output, which includes the
 * name of the output and its associated kinds.
 */
@Data
public class DynamicBlockOutputResponse implements RoboflowResponseData {

    /**
     * The name of the dynamic block output.
     */
    private String name;

    /**
     * A list of {@link Kind} objects representing the different kinds associated with the output.
     * <br> Each kind provides additional information about the structure or type of the output.
     */
    private List<Kind> kind;
}
