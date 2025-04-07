package nusri.fyp.demo.roboflow.data.response;

import lombok.Data;
import nusri.fyp.demo.roboflow.data.AnyData;
import nusri.fyp.demo.roboflow.data.RoboflowResponseData;

import java.util.List;

/**
 * Represents a description of multiple models in the Roboflow API.
 * <br> This class contains the list of models available, typically describing the model metadata.
 * <br> The `models` field holds the data for each model, which can be expanded to a specific model type if needed.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@Data
public class ModelsDescriptions implements RoboflowResponseData {

    /**
     * A list of {@link AnyData} objects representing the descriptions of the models.
     * <br> Each item in the list provides metadata or details about a specific model.
     * <br> This can be replaced with a more specific model type if available.
     */
    private List<AnyData> models;

    /**
     * Returns a string representation of the {@link ModelsDescriptions} object.
     * <br> The string includes the number of models and their descriptions.
     *
     * @return A string representation of the model descriptions.
     */
    @Override
    public String toString() {
        return "ModelsDescriptions [size=" + models.size() +", models=" + models + "]";
    }
}
