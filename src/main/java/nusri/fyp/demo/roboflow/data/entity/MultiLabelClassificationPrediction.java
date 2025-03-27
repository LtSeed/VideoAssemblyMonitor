package nusri.fyp.demo.roboflow.data.entity;

import lombok.Data;

/**
 * Prediction for multi-label classification.
 * <br> This class encapsulates a prediction made by a multi-label classification model,
 * including the confidence level and the corresponding class ID for the predicted label.
 */
@Data
public class MultiLabelClassificationPrediction {

    /**
     * The confidence level of the prediction.
     * <br> This value represents how confident the model is in the predicted class.
     * A higher value indicates greater confidence.
     */
    private float confidence;

    /**
     * The class ID for the predicted label.
     * <br> This ID corresponds to the label in the model's class list and is used to
     * identify which class the model has assigned to the input.
     */
    private int classId;
}
