package nusri.fyp.demo.roboflow.data.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nusri.fyp.demo.roboflow.data.RoboflowRequestData;
import nusri.fyp.demo.roboflow.data.entity.DynamicBlockDefinition;

/**
 * Represents a request to describe blocks within a workflow.
 * <br> This request is used to fetch the description of blocks that are part of a workflow,
 * including dynamic block definitions and the execution engine version.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WorkflowsBlocksDescriptionRequest extends RoboflowRequestData {

    /**
     * The API key required to authenticate the request.
     * <br> This field is necessary for accessing the workflow blocks description API.
     */
    private String apiKey;

    /**
     * The dynamic block definitions to be included in the workflow block description.
     * <br> This field contains the definition of dynamic blocks within the workflow,
     * which are usually used to describe variable or flexible block behavior.
     */
    private DynamicBlockDefinition dynamicBlocksDefinitions;

    /**
     * The version of the execution engine to be used for processing the workflow.
     * <br> This field specifies which version of the execution engine should be used for the workflow blocks.
     */
    private String executionEngineVersion;
}
