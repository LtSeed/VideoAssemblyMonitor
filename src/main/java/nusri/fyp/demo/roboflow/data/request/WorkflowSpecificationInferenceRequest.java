package nusri.fyp.demo.roboflow.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import nusri.fyp.demo.roboflow.data.AnyData;
import nusri.fyp.demo.roboflow.data.RoboflowRequestData;
import nusri.fyp.demo.roboflow.data.entity.InferenceRequestImage;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a request for workflow specification inference.
 * <br> This request is used to trigger an inference process using a specified workflow.
 * It includes various inputs and specification details necessary for the inference process.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WorkflowSpecificationInferenceRequest extends RoboflowRequestData {

    /**
     * The API key required to authenticate the request.
     * <br> This field is used to authenticate the request and gain access to the workflow inference functionality.
     */
    @JsonProperty("api_key")
    private String apiKey;

    /**
     * A dictionary that contains each parameter defined as an input for the chosen workflow.
     * <br> This map includes all the necessary inputs for the inference process, such as images or any other relevant data.
     */
    private Map<String, RoboflowRequestData> inputs = new HashMap<>();

    /**
     * The ID of the workflow to be used for inference.
     * <br> This field specifies which workflow to use for the inference process.
     */
    @JsonProperty("workflow_id")
    private String workflowId;

    /**
     * A map that contains the specification of the workflow.
     * <br> This field includes the detailed specifications for the workflow being triggered, such as configuration settings.
     */
    private Map<String, Object> specification;

    /**
     * Whether this request is a preview of the workflow.
     * <br> If set to true, this request represents a preview of the workflow, typically used for testing or validation.
     */
    @JsonProperty("is_preview")
    private Boolean isPreview;

    /**
     * Adds an image to the workflow inputs for inference.
     * <br> This method adds an `InferenceRequestImage` object as the "image" input to the request.
     *
     * @param data The image data to be included as input for the workflow.
     */
    public void addImage(InferenceRequestImage data) {
        inputs.put("image", data);
    }
}
