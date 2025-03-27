package nusri.fyp.demo.roboflow.data.entity;

import lombok.Data;

/**
 * Prediction for object detection.
 * <br> This class encapsulates a prediction made by an object detection model, including the
 * bounding box coordinates, confidence level, class information, and detection identifiers.
 */
@Data
public class ObjectDetectionPrediction {

    /**
     * The x-coordinate of the top-left corner of the bounding box.
     * <br> This is the horizontal position of the detected object's bounding box.
     */
    private float x;

    /**
     * The y-coordinate of the top-left corner of the bounding box.
     * <br> This is the vertical position of the detected object's bounding box.
     */
    private float y;

    /**
     * The width of the bounding box.
     * <br> This represents the horizontal size of the detected object's bounding box.
     */
    private float width;

    /**
     * The height of the bounding box.
     * <br> This represents the vertical size of the detected object's bounding box.
     */
    private float height;

    /**
     * The confidence level of the detection.
     * <br> This value represents how confident the model is in the presence of the object.
     * A higher value indicates greater confidence in the detection.
     */
    private float confidence;

    /**
     * The class name of the detected object.
     * <br> This string represents the label or category of the object, e.g., "car", "person", etc.
     */
    private String className;

    /**
     * The confidence level of the predicted class.
     * <br> This represents the model's confidence in the classification of the detected object.
     */
    private Float classConfidence;

    /**
     * The class ID for the predicted label.
     * <br> This ID corresponds to the label in the model's class list and is used to
     * identify which class the object belongs to.
     */
    private int classId;

    /**
     * The unique identifier for the detection.
     * <br> This is a unique identifier for the object detection instance.
     */
    private String detectionId;

    /**
     * The parent ID of the detection.
     * <br> This ID associates the detection with a parent object in a hierarchy (if applicable).
     */
    private String parentId;
}
