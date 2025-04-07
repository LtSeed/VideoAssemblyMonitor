package nusri.fyp.demo.roboflow.data.response;

import lombok.Data;
import nusri.fyp.demo.roboflow.data.AnyData;
import nusri.fyp.demo.roboflow.data.RoboflowResponseData;

import java.util.List;

/**
 * Represents a validation error returned by the Roboflow API.
 * <br> This class contains information about the location of the error, the error message, and the type of error.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@Data
public class ValidationError implements RoboflowResponseData {

    /**
     * A list of {@link AnyData} objects indicating the location(s) of the error.
     * <br> This could represent specific fields or parts of the request that triggered the validation error.
     */
    private List<AnyData> loc;

    /**
     * A message describing the validation error.
     * <br> This field provides details about why the validation failed.
     */
    private String msg;

    /**
     * The type of validation error.
     * <br> This could represent the error's category, such as "missing_field", "invalid_format", etc.
     */
    private String type;
}
