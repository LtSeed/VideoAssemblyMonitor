package nusri.fyp.demo.roboflow.data.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nusri.fyp.demo.roboflow.data.RoboflowRequestData;
import nusri.fyp.demo.roboflow.data.entity.InferenceRequestImage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a request for inference using a predefined workflow.
 * <br> This class is used to send a request to the Roboflow API to perform inference using a predefined workflow.
 * The request includes necessary inputs, parameters, and configuration options for the inference process.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PredefinedWorkflowInferenceRequest extends RoboflowRequestData {

    /**
     * The API key required to authenticate the request.
     * <br> This field is necessary for authentication and to associate the request with a specific user account.
     */
    private String apiKey;

    /**
     * A dictionary containing each parameter defined as an input for the chosen workflow.
     * <br> This object includes the input parameters required by the predefined workflow.
     */
    private Map<String, RoboflowRequestData> inputs = new HashMap<>(); // Replace Object with a specific type if available

    /**
     * A list of fields to be excluded from the inference request.
     * <br> This allows users to exclude specific fields or data from being processed in the workflow.
     */
    private List<String> excludedFields;

    /**
     * A flag to enable profiling during the workflow inference.
     * <br> When enabled, profiling data will be collected and can be used for performance analysis.
     */
    private boolean enableProfiling;

    /**
     * The ID of the predefined workflow to be used for inference.
     * <br> This field identifies the specific workflow model that will be applied to the inference request.
     */
    private String workflowId;

    /**
     * A flag to indicate whether to use a cached version of the workflow results.
     * <br> If true, the system will use cached results for faster processing, if available.
     */
    private boolean useCache;

    public void addImage(InferenceRequestImage image) {
        inputs.put("image", image);
    }
}
