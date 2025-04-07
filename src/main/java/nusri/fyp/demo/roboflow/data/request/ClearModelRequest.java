package nusri.fyp.demo.roboflow.data.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nusri.fyp.demo.roboflow.data.RoboflowRequestData;

/**
 * Represents a request to clear (remove) a model from the inference server in the Roboflow API.
 * <br> This class is used to send a request to the Roboflow API to remove a model from the inference server using the model's unique ID.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClearModelRequest extends RoboflowRequestData {

    /**
     * The unique identifier for the model to be removed.
     * <br> This field is required to specify which model should be cleared (removed) from the inference server.
     */
    private String modelId;
}
