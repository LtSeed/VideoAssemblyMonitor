package nusri.fyp.demo.roboflow.data.entity;

import lombok.Data;

/**
 * Represents the definition of a dynamic block.
 * <br> A dynamic block is a component of a workflow or model that can be configured
 * to perform a certain operation, such as data processing or transformation.
 */
@Data
public class DynamicBlockDefinition {

    /**
     * The type of the dynamic block.
     * <br> This field specifies the kind of block (e.g., "processing", "input", "output").
     */
    private String type;

    /**
     * The manifest description of the dynamic block.
     * <br> This field provides detailed metadata and configuration for the dynamic block,
     * describing how the block should be executed or used in a workflow.
     */
    private ManifestDescription manifest;

    /**
     * The Python code associated with the dynamic block.
     * <br> This field contains the code that defines the functionality or behavior of the dynamic block.
     */
    private PythonCode code;
}
