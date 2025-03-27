package nusri.fyp.demo.roboflow.data.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nusri.fyp.demo.roboflow.data.RoboflowRequestData;

/**
 * Represents a request to describe the interface of a predefined workflow.
 * <br> This class is used to send a request to the Roboflow API to retrieve information about the interface of a predefined workflow,
 * which may include inputs, outputs, and other relevant details about how the workflow can be interacted with.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PredefinedWorkflowDescribeInterfaceRequest extends RoboflowRequestData {

    /**
     * The API key required to authenticate the request.
     * <br> This field is necessary to associate the request with a specific account and for authorization purposes.
     */
    private String apiKey;

    /**
     * A flag indicating whether to use a cached version of the interface description.
     * <br> If true, the system may return a cached version of the workflow interface description, if available,
     * instead of fetching a fresh version from the server.
     */
    private boolean useCache;
}
