package nusri.fyp.demo.roboflow.data.entity;

import lombok.Data;
import java.util.List;

/**
 * Prediction details for detected keypoints, including bounding box, confidence, and individual keypoints.
 * <br> This class is used in tasks where the goal is to detect multiple keypoints in an image, such as human pose estimation or object keypoint detection.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@Data
public class KeypointsPrediction {

    /**
     * The x-coordinate of the bounding box.
     * <br> This represents the horizontal position of the top-left corner of the bounding box that contains the keypoints.
     */
    private Double x;

    /**
     * The y-coordinate of the bounding box.
     * <br> This represents the vertical position of the top-left corner of the bounding box that contains the keypoints.
     */
    private Double y;

    /**
     * The width of the bounding box.
     * <br> This represents the horizontal size of the bounding box that contains the keypoints.
     */
    private Double width;

    /**
     * The height of the bounding box.
     * <br> This represents the vertical size of the bounding box that contains the keypoints.
     */
    private Double height;

    /**
     * The confidence score for the prediction.
     * <br> This value indicates how likely the detected keypoints and the bounding box are correct.
     */
    private Double confidence;

    /**
     * The class name of the object being detected (e.g., "person", "car").
     * <br> This represents the type of object associated with the detected keypoints.
     */
    private String className;

    /**
     * The confidence score for the class detection.
     * <br> This value indicates how likely the class label is correct for the detected object.
     */
    private Double classConfidence;

    /**
     * The class ID associated with the prediction (e.g., 1 for "person", 2 for "car").
     * <br> This is a numerical identifier for the class of the detected object.
     */
    private Integer classId;

    /**
     * The tracker ID for the detection.
     * <br> This value is used for tracking the object over time in video or multi-frame inference tasks.
     */
    private Integer trackerId;

    /**
     * The detection ID for the prediction.
     * <br> This is a unique identifier for the specific detection, often used for referencing or correlating detections.
     */
    private String detectionId;

    /**
     * The parent ID for the prediction.
     * <br> This value is used to link the current detection to a higher-level entity (e.g., a human pose being tracked).
     */
    private String parentId;

    /**
     * A list of keypoints detected within the bounding box.
     * <br> This represents the individual keypoints identified in the image (e.g., joints for pose estimation).
     */
    private List<Keypoint> keypoints;
}
