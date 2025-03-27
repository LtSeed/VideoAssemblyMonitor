package nusri.fyp.demo.roboflow.data.entity;

import lombok.Data;

/**
 * Represents the definition of an external block property primitive.
 * <br> An external block property primitive defines a property associated with a block, such as its type, name,
 * and description. These properties are used to configure or interact with blocks in a workflow.
 */
@Data
public class ExternalBlockPropertyPrimitiveDefinition {

    /**
     * The identifier of the manifest type for this external block property.
     * <br> This field specifies the unique identifier that corresponds to the type of manifest
     * associated with the external block property.
     */
    private String manifestTypeIdentifier;

    /**
     * The name of the external block property.
     * <br> This field specifies the name of the property, which is used to reference the property in workflows.
     */
    private String propertyName;

    /**
     * The description of the external block property.
     * <br> This field provides a detailed explanation of the property's purpose or functionality.
     */
    private String propertyDescription;

    /**
     * The type annotation of the external block property.
     * <br> This field specifies the data type or class associated with the property, providing information
     * on how the property should be processed or used.
     */
    private String typeAnnotation;
}
