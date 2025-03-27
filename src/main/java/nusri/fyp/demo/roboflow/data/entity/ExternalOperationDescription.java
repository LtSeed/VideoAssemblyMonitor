package nusri.fyp.demo.roboflow.data.entity;

import lombok.Data;
import java.util.List;

/**
 * Represents the description of an external operation.
 * <br> An external operation defines an operation performed within a workflow or processing pipeline,
 * including the types of inputs and outputs it handles, whether it is a compound operation,
 * and additional nested operations it may include.
 */
@Data
public class ExternalOperationDescription {

    /**
     * The type of the operation.
     * <br> This field specifies the kind or type of operation being described, such as "transformation", "filter", etc.
     */
    private String operationType;

    /**
     * Indicates if the operation is compound.
     * <br> A compound operation is an operation that consists of multiple sub-operations,
     * while a non-compound operation is a single operation.
     */
    private Boolean compound;

    /**
     * A list of input kinds for the operation.
     * <br> This field specifies the types or kinds of inputs that the operation accepts.
     */
    private List<String> inputKind;

    /**
     * A list of output kinds for the operation.
     * <br> This field specifies the types or kinds of outputs that the operation produces.
     */
    private List<String> outputKind;

    /**
     * A list of input kinds for nested operations within this operation.
     * <br> If the operation contains nested operations, this field specifies the kinds of inputs expected
     * by the nested operations.
     */
    private List<String> nestedOperationInputKind;

    /**
     * A list of output kinds for nested operations within this operation.
     * <br> If the operation contains nested operations, this field specifies the kinds of outputs produced
     * by the nested operations.
     */
    private List<String> nestedOperationOutputKind;

    /**
     * A description of the operation.
     * <br> This field provides a detailed explanation of the operation's functionality, purpose,
     * and how it interacts within the workflow.
     */
    private String description;
}
