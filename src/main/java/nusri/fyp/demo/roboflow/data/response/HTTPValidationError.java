package nusri.fyp.demo.roboflow.data.response;

import lombok.Data;
import nusri.fyp.demo.roboflow.data.AnyData;
import nusri.fyp.demo.roboflow.data.RoboflowResponseData;

import java.util.List;
import java.util.Map;

/**
 * Represents a validation error returned in HTTP responses from the Roboflow API.
 * <br> This class contains the error details typically returned when the request fails due to invalid data or parameters.
 * <br> The `detail` field holds the specifics of the validation error.
 */
@Data
public class HTTPValidationError implements RoboflowResponseData {

    /**
     * A list of maps representing the details of the validation error.
     * <br> Each map contains key-value pairs where the key is a field or parameter, and the value is the associated error description.
     * <br> The value is wrapped in an {@link AnyData} object.
     */
    private List<Map<String, AnyData>> detail; // Could also use a specific detail class if needed.
}
