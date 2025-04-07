package nusri.fyp.demo.roboflow.data.response;

import lombok.Data;
import nusri.fyp.demo.roboflow.data.AnyData;
import nusri.fyp.demo.roboflow.data.RoboflowResponseData;

/**
 * Represents the schema description of workflow blocks in the Roboflow API.
 * <br> This class provides the schema for the blocks within a workflow, which defines the structure and requirements of the blocks.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@Data
public class WorkflowsBlocksSchemaDescription implements RoboflowResponseData {

    /**
     * An {@link AnyData} object that holds the schema for the workflow blocks.
     * <br> This schema provides the structural definition for the blocks in the workflow, including required fields and data types.
     */
    private AnyData schema;
}
