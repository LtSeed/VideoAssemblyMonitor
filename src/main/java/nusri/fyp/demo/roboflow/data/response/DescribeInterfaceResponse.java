package nusri.fyp.demo.roboflow.data.response;

import lombok.Data;
import nusri.fyp.demo.roboflow.data.AnyData;
import nusri.fyp.demo.roboflow.data.RoboflowResponseData;

import java.util.List;
import java.util.Map;

/**
 * Represents the response for describing an interface in the Roboflow API.
 * <br> This class contains the data returned by the API when querying for details about an interface,
 * including input parameters, output data, typing hints, and schema information.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@Data
public class DescribeInterfaceResponse implements RoboflowResponseData {

    /**
     * A map of input parameters where the key is the input name and the value is a list of accepted input types.
     */
    private Map<String, List<String>> inputs;

    /**
     * A map of output parameters where the key is the output name and the value is an {@link AnyData} object,
     * representing the raw output data for each parameter.
     */
    private Map<String, AnyData> outputs;

    /**
     * A map of typing hints where the key is the parameter name and the value is a string providing typing information.
     * <br> This can be used to suggest the expected type for each input or output.
     */
    private Map<String, String> typingHints;

    /**
     * A map of kind schemas where the key is the parameter name and the value is an {@link AnyData} object,
     * representing the schema for each parameter, typically for structured or complex types.
     */
    private Map<String, AnyData> kindsSchemas;
}
