package nusri.fyp.demo.roboflow.data.entity;

import lombok.Data;

/**
 * Represents a single keypoint with its coordinates, confidence, and associated class information.
 * <br> This class is used in keypoint detection tasks where the goal is to locate specific points (e.g., joints in a human pose) in an image.
 */
@Data
public class Keypoint {

    /**
     * The x-coordinate of the keypoint.
     * <br> This represents the horizontal position of the keypoint in the image.
     */
    private Double x;

    /**
     * The y-coordinate of the keypoint.
     * <br> This represents the vertical position of the keypoint in the image.
     */
    private Double y;

    /**
     * The confidence score for the keypoint detection.
     * <br> This value represents the likelihood that the detected point is correct.
     */
    private Double confidence;

    /**
     * The class ID associated with the keypoint.
     * <br> This could be used to identify the type of point, for example, "elbow", "knee", etc., in human pose estimation.
     */
    private Integer classId;

    /**
     * The class name associated with the keypoint.
     * <br> For example, "elbow", "knee", etc., depending on the keypoint detection task.
     */
    private String className;
}
