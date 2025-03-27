package nusri.fyp.demo.roboflow.data.entity.workflow;

import lombok.Data;

import java.util.List;

/**
 * Represents the predictions for an image in a workflow inference.
 * <br> The predictions include the image data and a list of individual predictions made on the image.
 * Example:
 * <pre>
 * "predictions": {
 *   "image": {...},
 *   "predictions": [...]
 * }
 * </pre>
 */
@Data
public class Predictions {

    /**
     * The image data associated with the predictions.
     */
    private ImageData image;

    /**
     * The list of individual predictions made on the image.
     */
    private List<SinglePrediction> predictions;

}
