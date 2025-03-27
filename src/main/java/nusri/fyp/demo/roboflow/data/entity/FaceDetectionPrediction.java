package nusri.fyp.demo.roboflow.data.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import nusri.fyp.demo.roboflow.data.AnyData;

import java.util.List;

/**
 * Represents a face detection prediction in an image or video frame.
 * <br> This class holds the data related to a face detected in an image, such as the bounding box coordinates,
 * confidence score, and additional properties like class name and landmarks.
 */
@Data
public class FaceDetectionPrediction {

    /**
     * The x-coordinate of the top-left corner of the bounding box.
     * <br> This field specifies the horizontal position of the bounding box for the detected face.
     */
    private float x;

    /**
     * The y-coordinate of the top-left corner of the bounding box.
     * <br> This field specifies the vertical position of the bounding box for the detected face.
     */
    private float y;

    /**
     * The width of the bounding box.
     * <br> This field represents the width of the bounding box surrounding the detected face.
     */
    private float width;

    /**
     * The height of the bounding box.
     * <br> This field represents the height of the bounding box surrounding the detected face.
     */
    private float height;

    /**
     * The confidence score of the face detection.
     * <br> This field indicates the model's confidence in detecting the face, with higher values representing greater confidence.
     */
    private float confidence;

    /**
     * The class of the object detected.
     * <br> The value is set to "face" for face detection. This field is annotated with {@link JsonProperty} to
     * map the JSON property "class" to the `clazz` field.
     */
    @JsonProperty("class")
    private String clazz;

    /**
     * The confidence score for the detected object's class.
     * <br> This field represents the model's confidence in the class prediction for the detected face.
     */
    private Float class_confidence;

    /**
     * The class ID of the detected object.
     * <br> This field holds the ID of the class of the detected object (face in this case).
     */
    private Float class_id;

    /**
     * The tracker ID of the detected face.
     * <br> This field identifies the specific instance of the detected face being tracked.
     */
    private Float tracker_id;

    /**
     * A unique identifier for the detection.
     * <br> This field stores the detection ID for the specific face detection.
     */
    private String detection_id;

    /**
     * The parent ID associated with the face detection.
     * <br> This field stores the parent ID if the face detection is part of a larger set of detections.
     */
    private String parent_id;

    /**
     * The class name of the detected object, which is set to "face" by default.
     * <br> This field represents the type of object detected, which in this case is always a face.
     */
    private String className = "face";

    /**
     * Landmarks associated with the detected face.
     * <br> This field represents the key points of the detected face, such as the eyes, nose, and mouth, stored as a list of objects.
     * The specific types of the objects can be adjusted based on the requirements.
     */
    private List<AnyData> landmarks; // This can be adjusted to more specific types
}
