package nusri.fyp.demo.roboflow.data.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nusri.fyp.demo.roboflow.data.RoboflowRequestData;
import nusri.fyp.demo.roboflow.data.entity.InferenceRequestImage;

import java.util.List;

/**
 * Request object for performing keypoints detection inference.
 * <br> This class represents a request to perform keypoints detection inference using the Roboflow API.
 * Keypoints detection involves identifying specific points (such as joints in a human body or key parts of objects) in an image.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class KeypointsDetectionInferenceRequest extends RoboflowRequestData {

    /**
     * The unique identifier for the inference request.
     * <br> This is used to reference and track the specific keypoints detection inference request.
     */
    private String id;

    /**
     * The API key associated with the request.
     * <br> This is required for authentication and to associate the request with a specific account.
     */
    private String apiKey;

    /**
     * A flag indicating whether the inference usage is billable.
     * <br> This flag specifies whether the request will incur charges.
     */
    private Boolean usageBillable;

    /**
     * The start time of the inference request (in seconds).
     * <br> This field indicates when the keypoints detection inference started.
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
     * <br> This field specifies which model will process the image for keypoints detection.
     */
    private String modelId;

    /**
     * The model type used for the inference process.
     * <br> This field specifies the type of the model, which can help differentiate between multiple models if applicable.
     */
    private String modelType;

    /**
     * The image to be processed for keypoints detection.
     * <br> This field can be an array of objects or a single object representing the image data.
     * The image will be analyzed to identify keypoints of interest.
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
     * A flag indicating whether class-agnostic Non-Maximum Suppression (NMS) should be used.
     * <br> When enabled, the NMS will not consider class labels when suppressing overlapping bounding boxes.
     */
    private Boolean classAgnosticNms;

    /**
     * A list of class labels to filter the detected keypoints.
     * <br> This field specifies which classes should be considered for keypoints detection.
     */
    private List<String> classFilter;

    /**
     * The confidence threshold for the keypoint predictions.
     * <br> This field specifies the minimum confidence required for a predicted keypoint to be considered valid.
     */
    private Double confidence;

    /**
     * A flag to fix the batch size during inference.
     * <br> This flag specifies whether to fix the batch size during inference processing.
     */
    private Boolean fixBatchSize;

    /**
     * The Intersection over Union (IoU) threshold for filtering predictions.
     * <br> This threshold determines the overlap required for predictions to be considered valid.
     */
    private Double iouThreshold;

    /**
     * The maximum number of detections to consider.
     * <br> This field specifies the maximum number of keypoints that should be detected.
     */
    private Integer maxDetections;

    /**
     * The maximum number of candidate detections to consider.
     * <br> This field specifies the maximum number of candidate detections to process before applying NMS.
     */
    private Integer maxCandidates;

    /**
     * A flag indicating whether labels should be visualized alongside the keypoints.
     * <br> This flag determines whether the predicted class labels should be displayed alongside the keypoints.
     */
    private Boolean visualizationLabels;

    /**
     * The stroke width for visualizing the keypoints.
     * <br> This field specifies the width of the outline used for drawing the keypoints.
     */
    private Integer visualizationStrokeWidth;

    /**
     * A flag indicating whether the predictions should be visualized.
     * <br> This flag determines if the keypoints detection results should be visualized.
     */
    private Boolean visualizePredictions;

    /**
     * A flag indicating whether active learning should be disabled during inference.
     * <br> This flag determines if active learning should be turned off for this inference request.
     */
    private Boolean disableActiveLearning;

    /**
     * The target dataset for active learning.
     * <br> This field specifies the dataset to use for active learning during inference.
     */
    private String activeLearningTargetDataset;

    /**
     * The confidence threshold for the keypoints.
     * <br> This field specifies the minimum confidence required for a detected keypoint to be considered valid.
     */
    private Double keypointConfidence;
}
