package nusri.fyp.demo.roboflow.data.entity.workflow.dto;

import lombok.Data;
import nusri.fyp.demo.roboflow.data.entity.workflow.Predictions;

import java.util.List;

/**
 * Represents the main structure of an external JSON response.
 * For example:
 * <pre>
 * {
 *   "image": {...},
 *   "predictions": [...],
 * }
 * </pre>
 * <br>
 * Can be converted to an entity {@link nusri.fyp.demo.roboflow.data.entity.workflow.Predictions}.
 */
@Data
public class PredictionsDTO {
    /**
     * Represents the image information associated with the prediction (e.g., width and height).
     */
    private ImageDataDTO image;

    /**
     * A list of predictions, each corresponding to a bounding box or detected target.
     */
    private List<SinglePredictionDTO> predictions;

    /**
     * Converts the current DTO to an entity {@link nusri.fyp.demo.roboflow.data.entity.workflow.Predictions}.
     *
     * @return The converted entity object.
     */
    public Predictions toEntity() {
        Predictions predictions = new Predictions();
        predictions.setImage(image.toEntity());
        predictions.setPredictions(this.predictions.stream().map(SinglePredictionDTO::toEntity).toList());
        return predictions;
    }
}