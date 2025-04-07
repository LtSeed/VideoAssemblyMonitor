package nusri.fyp.demo.roboflow.data.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nusri.fyp.demo.roboflow.data.RoboflowRequestData;
import nusri.fyp.demo.roboflow.data.entity.InferenceRequestImage;

/**
 * Represents a request for a SAM (Segment Anything Model) embedding.
 * <br> This request is used to send an image to the SAM model for embedding. The request includes parameters like image data, model configurations, and other related settings for the embedding process.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SamEmbeddingRequest extends RoboflowRequestData {

    /**
     * The unique identifier for the request.
     * <br> This field is used to track and reference the request.
     */
    private String id;

    /**
     * The API key required to authenticate the request.
     * <br> This field is necessary for authorization and ensuring that the request is valid.
     */
    private String apiKey;

    /**
     * A flag indicating whether the usage of this request should be billed.
     * <br> If true, the request will count against the user's usage limits.
     */
    private boolean usageBillable;

    /**
     * The start time for the inference process, typically used for tracking or logging.
     * <br> This field is optional and can be used for timing the inference process.
     */
    private Double start;

    /**
     * The source identifier of the image being processed.
     * <br> This field can be used to reference the source of the image, such as a URL or local path.
     */
    private String source;

    /**
     * Additional information about the source of the image.
     * <br> This field is used for supplementary information related to the source.
     */
    private String sourceInfo;

    /**
     * The version ID of the SAM model being used for embedding.
     * <br> This field specifies which version of the SAM model should be used for the request.
     */
    private String samVersionId;

    /**
     * The model ID of the SAM model to be used.
     * <br> This field is used to specify the exact model for embedding.
     */
    private String modelId;

    /**
     * The image to be processed for embedding.
     * <br> This field represents the input image that will be used for embedding using the SAM model.
     */
    private InferenceRequestImage image; // Replace Object with a specific type if available

    /**
     * The unique identifier for the image being processed.
     * <br> This field is used to reference the specific image in the request.
     */
    private String imageId;

    /**
     * The format for the output embedding result.
     * <br> This can be a format such as 'png', 'jpg', or other relevant types for the output image.
     */
    private String format;
}
