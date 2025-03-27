package nusri.fyp.demo.roboflow.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A class representing a generic response or request data container for Roboflow API operations.
 * <br> This class extends {@link RoboflowRequestData} and implements {@link RoboflowResponseData}.
 * <br> It is used for holding raw data as a string, typically for scenarios where the response is not specifically typed.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnyData extends RoboflowRequestData implements RoboflowResponseData {

    /**
     * The raw data as a string.
     * <br> This field holds the data received from or sent to the Roboflow API.
     */
    String data;

    /**
     * Constructor to initialize the {@link AnyData} object with the provided data.
     *
     * @param data The raw data as a string.
     */
    public AnyData(String data) {
        this.data = data;
    }
}
