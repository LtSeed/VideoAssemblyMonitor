package nusri.fyp.demo.roboflow.data.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nusri.fyp.demo.roboflow.data.RoboflowRequestData;
import nusri.fyp.demo.roboflow.data.entity.InferenceRequestImage;

/**
 * Represents a classification inference request in the Roboflow API.
 * <br> This class is used to send a request to the Roboflow API to perform classification inference on an image,
 * providing necessary parameters such as model ID, input image, preprocessing options, and more.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClassificationInferenceRequest extends RoboflowRequestData {

    /**
     * The unique identifier for the inference request.
     * <br> This field is used to reference and track the specific inference request.
     */
    private String id;

    /**
     * The API key associated with the request.
     * <br> This is required for authentication and to associate the request with a specific account.
     */
    private String apiKey;

    /**
     * A flag indicating whether the inference usage is billable.
     * <br> This field specifies whether the inference request will incur charges.
     */
    private Boolean usageBillable;

    /**
     * The start time of the inference request (in seconds).
     * <br> This field indicates when the classification inference started.
     */
    private Double start;

    /**
     * The source of the image or data being processed.
     * <br> This can describe where the image or data originates (e.g., "web", "camera", etc.).
     */
    private String source;

    /**
     * Additional information about the image source.
     * <br> This can provide context or metadata about the image source.
     */
    private String sourceInfo;

    /**
     * The ID of the model used for the classification inference.
     * <br> This is the identifier for the specific model that will process the request.
     */
    private String modelId;

    /**
     * The type of the model used for the classification inference.
     * <br> This can specify the model's type, such as "classification", "object_detection", etc.
     */
    private String modelType;

    /**
     * The image to be classified.
     * <br> This can be an array of objects or a single object representing the image data.
     */
    private InferenceRequestImage image;

    /**
     * A flag to disable automatic orientation adjustment during preprocessing.
     * <br> If set to `true`, automatic orientation will be disabled.
     */
    private Boolean disablePreprocAutoOrient;

    /**
     * A flag to disable contrast adjustment during preprocessing.
     * <br> If set to `true`, contrast adjustment will be disabled.
     */
    private Boolean disablePreprocContrast;

    /**
     * A flag to disable grayscale conversion during preprocessing.
     * <br> If set to `true`, grayscale conversion will be disabled.
     */
    private Boolean disablePreprocGrayscale;

    /**
     * A flag to disable static cropping during preprocessing.
     * <br> If set to `true`, static cropping will be disabled.
     */
    private Boolean disablePreprocStaticCrop;

    /**
     * The confidence threshold for predictions.
     * <br> This field specifies the minimum confidence required for a prediction to be considered valid.
     */
    private Float confidence;

    /**
     * The stroke width for visualizing predictions.
     * <br> This field defines the width of the lines used to draw the predicted bounding boxes or marks.
     */
    private Integer visualizationStrokeWidth;

    /**
     * A flag to visualize the predictions.
     * <br> If set to `true`, the predictions will be visualized on the image.
     */
    private Boolean visualizePredictions;

    /**
     * A flag to disable active learning.
     * <br> If set to `true`, active learning will be disabled for this request.
     */
    private Boolean disableActiveLearning;

    /**
     * The target dataset for active learning (if applicable).
     * <br> This field specifies the dataset to which active learning data will be sent.
     */
    private String activeLearningTargetDataset;
}
