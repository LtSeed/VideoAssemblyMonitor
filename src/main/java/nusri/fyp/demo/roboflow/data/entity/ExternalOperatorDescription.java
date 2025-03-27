package nusri.fyp.demo.roboflow.data.entity;

import lombok.Data;
import java.util.List;

/**
 * Represents the description of an external operator.
 * <br> An external operator defines an operation that is applied in a workflow or processing pipeline,
 * detailing the types and numbers of operands it requires, and a description of its function.
 */
@Data
public class ExternalOperatorDescription {

    /**
     * The type of the operator.
     * <br> This field specifies the type or kind of operator being described, such as "addition", "multiplication",
     * or "transformation", representing the operation performed by the operator.
     */
    private String operatorType;

    /**
     * The number of operands required by the operator.
     * <br> This field indicates how many operands (inputs) the operator expects to operate on.
     */
    private Integer operandsNumber;

    /**
     * A list of operand kinds for the operator.
     * <br> This field specifies the types or kinds of operands that the operator can accept.
     * It can be a list of lists, where each sublist represents a set of operand types expected for a particular operand.
     */
    private List<List<String>> operandsKinds;

    /**
     * A description of the operator.
     * <br> This field provides a detailed explanation of the operator’s functionality,
     * its purpose within the workflow, and how it operates on the operands.
     */
    private String description;
}
