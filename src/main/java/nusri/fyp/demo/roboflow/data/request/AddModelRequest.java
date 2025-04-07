package nusri.fyp.demo.roboflow.data.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nusri.fyp.demo.roboflow.data.RoboflowRequestData;

/**
 * Represents a request to add a model to the inference server in the Roboflow API.
 * <br> This class is used to send a request to the Roboflow API to add a model to the inference server,
 * providing necessary details such as the model's ID, type, and an optional API key.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AddModelRequest extends RoboflowRequestData {

    /**
     * The unique identifier for the model to be added.
     * <br> This field is required to specify which model is being added to the inference server.
     */
    private String modelId;

    /**
     * The type of the model being added (optional).
     * <br> This field specifies the model type (e.g., "object_detection", "classification", etc.).
     * If not provided, the model type may be inferred or considered as unspecified.
     */
    private String modelType;

    /**
     * The API key associated with the model (optional).
     * <br> This field is used for authentication or to associate the model with a specific user or application.
     * If not provided, the system may use a default or pre-configured API key.
     */
    private String apiKey;
}
