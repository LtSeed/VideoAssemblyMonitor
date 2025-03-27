package nusri.fyp.demo.roboflow.data.entity;

import lombok.Data;

/**
 * Represents a gaze detection prediction associated with a face.
 * <br> This class holds the information about the face being detected and the gaze angles (yaw and pitch).
 */
@Data
public class GazeDetectionPrediction {

    /**
     * The detected face associated with the gaze detection.
     * <br> This field contains the details of the detected face, such as its bounding box, confidence score,
     * class name, and landmarks. It is represented by the {@link FaceDetectionPrediction} class.
     */
    private FaceDetectionPrediction face;

    /**
     * The yaw angle of the gaze.
     * <br> This field represents the horizontal angle of the detected gaze, usually measured in degrees.
     */
    private float yaw;

    /**
     * The pitch angle of the gaze.
     * <br> This field represents the vertical angle of the detected gaze, usually measured in degrees.
     */
    private float pitch;
}
