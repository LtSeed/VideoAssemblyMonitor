package nusri.fyp.demo.roboflow.data.entity;

import lombok.Data;

/**
 * Represents a classification prediction.
 * <br> This class is used to hold the prediction results for classification tasks,
 * where an image or object is assigned to a particular class along with its associated confidence score.
 */
@Data
public class ClassificationPrediction {

    /**
     * The name of the predicted class.
     * <br> This value represents the label or category assigned to the object in the classification task.
     */
    private String className;

    /**
     * The ID of the predicted class.
     * <br> This value represents a unique identifier for the predicted class.
     */
    private Integer classId;

    /**
     * The confidence score of the prediction.
     * <br> This value represents the model's confidence in the prediction, typically ranging from 0.0 to 1.0.
     * Higher values indicate greater confidence.
     */
    private Float confidence;
}
