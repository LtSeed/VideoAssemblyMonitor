package nusri.fyp.demo.roboflow.data.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nusri.fyp.demo.roboflow.data.RoboflowRequestData;
import nusri.fyp.demo.roboflow.data.entity.InferenceRequestImage;

import java.util.List;

/**
 * Represents a request for CLIP image embedding in the Roboflow API.
 * <br> This class is used to send a request to the Roboflow API to perform CLIP image embedding,
 * which generates a feature vector representation of an image using the CLIP model.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ClipImageEmbeddingRequest extends RoboflowRequestData {

    /**
     * The unique identifier for the inference request.
     * <br> This field is used to reference and track the specific embedding request.
     */
    private String id;

    /**
     * The API key associated with the request.
     * <br> This is required for authentication and to associate the request with a specific account.
     */
    private String apiKey;

    /**
     * A flag indicating whether the inference usage is billable.
     * <br> This field specifies whether the request will incur charges.
     */
    private Boolean usageBillable;

    /**
     * The start time of the inference request (in seconds).
     * <br> This field indicates when the CLIP image embedding started.
     */
    private Double start;

    /**
     * The source of the image being processed.
     * <br> This can describe where the image originates from (e.g., "web", "camera", etc.).
     */
    private String source;

    /**
     * Additional information about the image source.
     * <br> This can provide context or metadata about the image source.
     */
    private String sourceInfo;

    /**
     * The CLIP version ID being used for the image embedding.
     * <br> This field specifies which version of the CLIP model should be used.
     */
    private String clipVersionId;

    /**
     * The model ID used for the embedding process.
     * <br> This field specifies which model will process the image embedding request.
     */
    private String modelId;

    /**
     * The image to be embedded.
     * <br> This field can be an array of objects or a single object representing the image data.
     * The image will be processed to generate its feature vector representation.
     */
    private InferenceRequestImage image;
}
