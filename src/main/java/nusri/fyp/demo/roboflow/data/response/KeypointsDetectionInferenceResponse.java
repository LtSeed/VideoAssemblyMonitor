package nusri.fyp.demo.roboflow.data.response;

import lombok.Data;
import nusri.fyp.demo.roboflow.data.RoboflowResponseData;
import nusri.fyp.demo.roboflow.data.entity.InferenceImageDimensions;
import nusri.fyp.demo.roboflow.data.entity.KeypointsPrediction;

import java.util.List;

/**
 * Represents the response for keypoints detection inference in the Roboflow API.
 * <br> This class contains the data returned by the API after performing keypoints detection inference on an image.
 * <br> The response includes the visualization, inference metadata, and a list of keypoints predictions.
 */
@Data
public class KeypointsDetectionInferenceResponse implements RoboflowResponseData {

    /**
     * The URL or path to the visualization of the keypoints detection results.
     * <br> This could be an image or another format showing the detected keypoints.
     */
    private String visualization;

    /**
     * The unique identifier for the keypoints detection inference request.
     */
    private String inferenceId;

    /**
     * The frame ID of the video or sequence in which the keypoints detection was performed, if applicable.
     */
    private Integer frameId;

    /**
     * The time taken to perform the keypoints detection inference, typically in seconds.
     */
    private Double time;

    /**
     * The dimensions of the image on which the keypoints detection inference was performed.
     * <br> This could be a single object or a list of objects if multiple images were processed.
     */
    private InferenceImageDimensions image;

    /**
     * A list of {@link KeypointsPrediction} objects representing the detected keypoints in the image.
     * <br> Each prediction corresponds to a detected keypoint and its associated data.
     */
    private List<KeypointsPrediction> predictions;
}
