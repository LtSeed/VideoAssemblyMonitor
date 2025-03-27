package nusri.fyp.demo.roboflow.data.response;

import lombok.Data;
import nusri.fyp.demo.roboflow.data.RoboflowResponseData;

/**
 * Represents the status of a workflow validation in the Roboflow API.
 * <br> This class provides the status of the workflow validation process, indicating whether the workflow is valid or not.
 */
@Data
public class WorkflowValidationStatus implements RoboflowResponseData {

    /**
     * The status of the workflow validation.
     * <br> This field indicates whether the workflow passed or failed validation.
     * <br> Possible values may include "valid", "invalid", or other status indicators based on the validation results.
     */
    private String status;
}
