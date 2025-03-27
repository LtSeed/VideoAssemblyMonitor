package nusri.fyp.demo.roboflow.data.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nusri.fyp.demo.roboflow.data.RoboflowRequestData;
import nusri.fyp.demo.roboflow.data.entity.InferenceRequestImage;
import nusri.fyp.demo.roboflow.data.entity.Sam2Prompt;

import java.util.List;

/**
 * Represents a request to perform segmentation using the SAM2 (Segment Anything Model) model.
 * <br> This request is used to send an image for segmentation using the SAM2 model. The request can include prompts,
 * configuration options for image format, and cache settings, among other parameters.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Sam2SegmentationRequest extends RoboflowRequestData {

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
     * The version ID of the SAM2 model being used for segmentation.
     * <br> This field specifies which version of the SAM2 model should be used for the request.
     */
    private String sam2VersionId;

    /**
     * The model ID of the SAM2 model to be used.
     * <br> This field is used to specify the exact model for segmentation.
     */
    private String modelId;

    /**
     * The format of the output segmentation result.
     * <br> This can be a format such as 'png', 'jpg', or other relevant types.
     */
    private String format;

    /**
     * The image to be processed for segmentation.
     * <br> This field represents the input image that will be used for segmentation using the SAM2 model.
     */
    private InferenceRequestImage image; // Replace Object with a specific type if available

    /**
     * The unique identifier for the image being processed.
     * <br> This field is used to reference the specific image in the request.
     */
    private String imageId;

    /**
     * A list of prompts to guide the segmentation process.
     * <br> This field allows users to specify specific regions or features in the image that should be segmented.
     */
    private List<Sam2Prompt> prompts; // Replace Object with a specific type if available

    /**
     * A flag indicating whether to output multiple masks for the segmentation result.
     * <br> If true, the system will generate multiple possible segmentation masks for the image.
     */
    private boolean multimaskOutput;

    /**
     * A flag to indicate whether to save the logits (raw model outputs) to cache.
     * <br> This can improve performance by allowing the logits to be reused in future requests.
     */
    private boolean saveLogitsToCache;

    /**
     * A flag to indicate whether to load the logits from cache.
     * <br> If true, the system will attempt to load cached logits for the image, if available.
     */
    private boolean loadLogitsFromCache;
}
