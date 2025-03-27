package nusri.fyp.demo.roboflow.data.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nusri.fyp.demo.roboflow.data.RoboflowRequestData;
import nusri.fyp.demo.roboflow.data.entity.InferenceRequestImage;

import java.util.List;

/**
 * Represents a request for SAM (Segment Anything Model) segmentation.
 * <br> This request is used to perform segmentation on an image using the SAM model. The request includes parameters such as image data, model configurations, embeddings, and mask information.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SamSegmentationRequest extends RoboflowRequestData {

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
     * The start time for the segmentation process, typically used for tracking or logging.
     * <br> This field is optional and can be used for timing the segmentation process.
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
     * The version ID of the SAM model being used for segmentation.
     * <br> This field specifies which version of the SAM model should be used for the request.
     */
    private String samVersionId;

    /**
     * The model ID of the SAM model to be used.
     * <br> This field is used to specify the exact model for segmentation.
     */
    private String modelId;

    /**
     * The embeddings to be used in the segmentation request.
     * <br> These embeddings represent spatial or feature information for segmentation.
     */
    private List<List<List<List<Float>>>> embeddings; // Replace Object with a specific type if available

    /**
     * The format for the embeddings provided.
     * <br> This specifies the format in which the embeddings are represented.
     */
    private String embeddingsFormat;

    /**
     * The format for the output segmentation result.
     * <br> This can be a format such as 'png', 'jpg', or other relevant types for the output image.
     */
    private String format;

    /**
     * The image to be processed for segmentation.
     * <br> This field represents the input image that will be used for segmentation using the SAM model.
     */
    private InferenceRequestImage image; // Replace Object with a specific type if available

    /**
     * The unique identifier for the image being processed.
     * <br> This field is used to reference the specific image in the request.
     */
    private String imageId;

    /**
     * A flag indicating whether mask input is provided.
     * <br> If true, the mask input will be used in the segmentation process.
     */
    private Boolean hasMaskInput;

    /**
     * The mask input data used for segmentation.
     * <br> This field provides the mask information that can influence the segmentation output.
     */
    private List<List<List<Float>>> maskInput; // Replace Object with a specific type if available

    /**
     * The format for the mask input provided.
     * <br> This specifies the format of the mask input data.
     */
    private String maskInputFormat;

    /**
     * The original image size, represented as a list of integers.
     * <br> This field specifies the original size of the image before any processing or resizing.
     */
    private List<Integer> origImSize; // Replace Integer with Double if appropriate

    /**
     * The coordinates of the points used for segmentation.
     * <br> This list contains the coordinates of key points used to guide the segmentation process.
     */
    private List<List<Double>> pointCoords;

    /**
     * The labels corresponding to the points used for segmentation.
     * <br> This field contains labels for the points specified in the `pointCoords` field.
     */
    private List<Double> pointLabels;

    /**
     * A flag indicating whether to use the cache for mask input.
     * <br> If true, the cached mask input will be used to optimize the segmentation process.
     */
    private Boolean useMaskInputCache;
}
