package nusri.fyp.demo.roboflow.data.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nusri.fyp.demo.roboflow.data.RoboflowRequestData;
import nusri.fyp.demo.roboflow.data.entity.InferenceRequestImage;

import java.util.List;

/**
 * Represents a request for Grounding DINO zero-shot predictions in the Roboflow API.
 * <br> This class is used to send a request to the Roboflow API to perform zero-shot object detection
 * using the Grounding DINO model, which predicts bounding boxes and associated labels without needing
 * task-specific training data.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GroundingDINOInferenceRequest extends RoboflowRequestData {

    /**
     * The unique identifier for the inference request.
     * <br> This field is used to reference and track the specific Grounding DINO inference request.
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
     * <br> This field indicates when the Grounding DINO inference started.
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
     * The model ID used for the inference process.
     * <br> This field specifies which model will process the image for zero-shot prediction.
     */
    private String modelId;

    /**
     * The model type used for the inference process.
     * <br> This field specifies the type of the model, which can help to differentiate between multiple
     * models if applicable.
     */
    private String modelType;

    /**
     * The image to be processed for zero-shot object detection.
     * <br> This field can be an array of objects or a single object representing the image data.
     * The image will be processed to detect objects and their bounding boxes.
     */
    private InferenceRequestImage image;

    /**
     * A flag to disable auto orientation preprocessing on the image.
     * <br> This flag determines if the image should be auto-oriented before processing.
     */
    private Boolean disablePreprocAutoOrient;

    /**
     * A flag to disable contrast preprocessing on the image.
     * <br> This flag determines if contrast adjustments should be applied to the image before processing.
     */
    private Boolean disablePreprocContrast;

    /**
     * A flag to disable grayscale preprocessing on the image.
     * <br> This flag determines if the image should be converted to grayscale before processing.
     */
    private Boolean disablePreprocGrayscale;

    /**
     * A flag to disable static crop preprocessing on the image.
     * <br> This flag determines if static cropping should be applied to the image before processing.
     */
    private Boolean disablePreprocStaticCrop;

    /**
     * The list of text queries to guide the zero-shot predictions.
     * <br> These queries help the model to predict specific objects based on the text input.
     */
    private List<String> text;

    /**
     * The threshold for the bounding boxes.
     * <br> This field specifies the minimum confidence required for a predicted bounding box to be considered valid.
     */
    private Double boxThreshold;

    /**
     * The version ID of the Grounding DINO model being used for inference.
     * <br> This field specifies which version of the Grounding DINO model should be used.
     */
    private String groundingDinoVersionId;

    /**
     * The threshold for the text query predictions.
     * <br> This field specifies the minimum confidence required for a text-based prediction to be considered valid.
     */
    private Double textThreshold;

    /**
     * A flag indicating whether class-agnostic Non-Maximum Suppression (NMS) should be used.
     * <br> When enabled, the NMS will not consider class labels when suppressing overlapping bounding boxes.
     */
    private Boolean classAgnosticNms;
}
