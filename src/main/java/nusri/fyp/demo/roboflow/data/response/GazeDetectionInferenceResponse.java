package nusri.fyp.demo.roboflow.data.response;

import lombok.Data;
import nusri.fyp.demo.roboflow.data.entity.GazeDetectionPrediction;
import nusri.fyp.demo.roboflow.data.RoboflowResponseData;

import java.util.List;

/**
 * Represents the response for gaze detection inference in the Roboflow API.
 * <br> This class contains the data returned by the API after performing gaze detection inference.
 * <br> The response includes the predictions made by the model and the time taken to perform the inference.
 */
@Data
public class GazeDetectionInferenceResponse implements RoboflowResponseData {

    /**
     * A list of {@link GazeDetectionPrediction} objects representing the predictions made by the model.
     * <br> Each prediction contains the results of a gaze detection inference.
     */
    private List<GazeDetectionPrediction> predictions;

    /**
     * The time taken to perform the gaze detection inference, typically in seconds.
     */
    private double time;
}
