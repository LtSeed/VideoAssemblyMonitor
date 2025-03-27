package nusri.fyp.demo.roboflow.data.response;

import lombok.Data;
import nusri.fyp.demo.roboflow.data.RoboflowResponseData;

import java.util.List;

/**
 * Represents the response for retrieving versions of the execution engine from the Roboflow API.
 * <br> This class contains a list of available execution engine versions, returned by the API.
 */
@Data
public class ExecutionEngineVersions implements RoboflowResponseData {

    /**
     * A list of strings representing the available versions of the execution engine.
     * <br> This list contains the version names or identifiers.
     */
    private List<String> versions;
}
