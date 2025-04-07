package nusri.fyp.demo.roboflow.data.response;

import lombok.Data;
import nusri.fyp.demo.roboflow.data.entity.MultiLabelClassificationPrediction;
import nusri.fyp.demo.roboflow.data.RoboflowResponseData;

import java.util.List;
import java.util.Map;

/**
 * Represents the response for multi-label classification inference in the Roboflow API.
 * <br> This class contains the data returned by the API after performing multi-label classification inference on an image.
 * <br> The response includes the predictions for each label and a list of the predicted classes.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@Data
public class MultiLabelClassificationInferenceResponse implements RoboflowResponseData {

    /**
     * A map of predictions, where the key is the label name and the value is the corresponding
     * {@link MultiLabelClassificationPrediction} object containing details for that label.
     */
    private Map<String, MultiLabelClassificationPrediction> predictions;

    /**
     * A list of classes predicted by the model during the multi-label classification inference.
     * <br> Each string in the list represents a predicted class label.
     */
    private List<String> predictedClasses;
}
