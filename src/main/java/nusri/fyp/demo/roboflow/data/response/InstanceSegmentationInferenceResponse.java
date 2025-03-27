package nusri.fyp.demo.roboflow.data.response;

import lombok.Data;
import nusri.fyp.demo.roboflow.data.entity.InstanceSegmentationPrediction;
import nusri.fyp.demo.roboflow.data.RoboflowResponseData;

import java.util.List;

/**
 * Represents the response for instance segmentation inference in the Roboflow API.
 * <br> This class contains the data returned by the API after performing instance segmentation inference on an image.
 */
@Data
public class InstanceSegmentationInferenceResponse implements RoboflowResponseData {

    /**
     * A list of {@link InstanceSegmentationPrediction} objects representing the predictions made by the model.
     * <br> Each prediction corresponds to a segmented object detected in the image.
     */
    private List<InstanceSegmentationPrediction> predictions;

    /**
     * The URL or path to the visualization of the instance segmentation results.
     * <br> This could be an image or other media format showing the segmented instances.
     */
    private String visualization;

    /**
     * The unique identifier for the inference request.
     */
    private String inferenceId;

    /**
     * The frame ID of the video or sequence in which the inference was made, if applicable.
     */
    private Integer frameId;

    /**
     * The time taken to perform the instance segmentation inference, typically in seconds.
     */
    private double time;
}
