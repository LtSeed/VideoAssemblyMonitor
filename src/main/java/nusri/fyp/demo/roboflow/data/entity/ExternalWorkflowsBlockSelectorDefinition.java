package nusri.fyp.demo.roboflow.data.entity;

import lombok.Data;

/**
 * Represents the definition of an external workflows block selector.
 * <br> This class describes a selector used in external workflow blocks, including information about its
 * manifest type, property name, compatibility with other elements, and its type (list or dictionary element).
 *
 * @author Liu Binghong
 * @since 1.0
 */
@Data
public class ExternalWorkflowsBlockSelectorDefinition {

    /**
     * The manifest type identifier for the selector.
     * <br> This field specifies the unique identifier for the manifest type associated with the selector.
     * It helps in defining the structure and type of data the selector operates on.
     */
    private String manifestTypeIdentifier;

    /**
     * The name of the property associated with the selector.
     * <br> This field represents the name of the property within the selector, helping to identify
     * the specific data being selected in the workflow block.
     */
    private String propertyName;

    /**
     * A description of the property associated with the selector.
     * <br> This field provides a detailed description of the property, explaining its role
     * and how it is used within the context of the workflow block.
     */
    private String propertyDescription;

    /**
     * The compatible element that the selector is associated with.
     * <br> This field specifies the element type that the selector is compatible with,
     * indicating which data structures or types the selector can interact with.
     */
    private String compatibleElement;

    /**
     * Indicates if the selector is an element of a list.
     * <br> If set to {@code true}, this field indicates that the selector refers to an element within a list.
     * If set to {@code false}, it indicates that the selector is not part of a list.
     */
    private Boolean isListElement;

    /**
     * Indicates if the selector is an element of a dictionary.
     * <br> If set to {@code true}, this field indicates that the selector refers to an element within a dictionary.
     * If set to {@code false}, it indicates that the selector is not part of a dictionary.
     */
    private Boolean isDictElement;
}
