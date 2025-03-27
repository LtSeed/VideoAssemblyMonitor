package nusri.fyp.demo.roboflow.data.response;

import lombok.Data;
import nusri.fyp.demo.roboflow.data.entity.ObjectDetectionPrediction;
import nusri.fyp.demo.roboflow.data.RoboflowResponseData;

import java.util.List;

/**
 * Represents the response for object detection inference in the Roboflow API.
 * <br> This class contains the data returned by the API after performing object detection inference on an image.
 * <br> The response includes a list of predictions for the detected objects.
 */
@Data
public class ObjectDetectionInferenceResponse implements RoboflowResponseData {

    /**
     * A list of {@link ObjectDetectionPrediction} objects representing the predictions made by the model.
     * <br> Each prediction corresponds to a detected object in the image, including details such as the label, bounding box, and confidence score.
     */
    private List<ObjectDetectionPrediction> predictions;
}
