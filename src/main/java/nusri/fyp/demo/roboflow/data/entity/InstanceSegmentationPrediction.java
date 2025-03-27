package nusri.fyp.demo.roboflow.data.entity;

import lombok.Data;

import java.util.List;

/**
 * Represents an instance segmentation prediction.
 * <br> This class is used to store the prediction results of an instance segmentation task.
 * It includes the bounding box coordinates, confidence scores, class information, and associated points for the detected instance.
 */
@Data
public class InstanceSegmentationPrediction {

    /**
     * The x-coordinate of the top-left corner of the bounding box.
     */
    private float x;

    /**
     * The y-coordinate of the top-left corner of the bounding box.
     */
    private float y;

    /**
     * The width of the bounding box.
     */
    private float width;

    /**
     * The height of the bounding box.
     */
    private float height;

    /**
     * The confidence score for the instance segmentation prediction.
     * <br> This value represents the likelihood that the detected object belongs to the predicted class.
     */
    private float confidence;

    /**
     * The class name of the detected object.
     * <br> For example, "person", "car", etc.
     */
    private String className;

    /**
     * The confidence score for the predicted class.
     * <br> This value represents the likelihood that the detected object belongs to the predicted class.
     */
    private Float classConfidence;

    /**
     * The class ID of the detected object.
     * <br> This is typically a numerical identifier for the class.
     */
    private int classId;

    /**
     * The unique detection ID for the instance.
     * <br> This can be used to track the individual detection.
     */
    private String detectionId;

    /**
     * The parent ID of the detected object.
     * <br> This could represent the relationship between instances in a hierarchical object structure.
     */
    private String parentId;

    /**
     * The list of points that define the segmentation mask for the object.
     * <br> These points are typically used to represent the precise shape of the object within the bounding box.
     */
    private List<Point> points;
}
