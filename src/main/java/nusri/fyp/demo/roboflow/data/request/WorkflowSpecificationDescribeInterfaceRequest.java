package nusri.fyp.demo.roboflow.data.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nusri.fyp.demo.roboflow.data.AnyData;
import nusri.fyp.demo.roboflow.data.RoboflowRequestData;

/**
 * Represents a request to describe a workflow specification interface.
 * <br> This request is used to fetch the details of a specified workflow interface,
 * typically including its structure and requirements, using a given specification.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WorkflowSpecificationDescribeInterfaceRequest extends RoboflowRequestData {

    /**
     * The API key required to authenticate the request.
     * <br> This field is used to authenticate the request and gain access to the workflow specification interface.
     */
    private String apiKey;

    /**
     * The specification of the workflow interface to be described.
     * <br> This field contains the details of the workflow interface, typically in the form of dynamic data.
     */
    private AnyData specification;
}
