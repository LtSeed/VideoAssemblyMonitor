package nusri.fyp.demo.roboflow.data.entity;

import lombok.Data;

/**
 * Represents a kind of data with its description and serialization details.
 * <br> This class is used to define the type and metadata of various data elements, including their internal and serialized representations.
 */
@Data
public class Kind {

    /**
     * The name of the data kind.
     * <br> This represents the type or category of the data (e.g., "image", "text").
     */
    private String name;

    /**
     * A brief description of the data kind.
     * <br> This provides additional information about what the data kind represents or how it should be used.
     */
    private String description;

    /**
     * Documentation or reference URL for the data kind.
     * <br> This can be a link to external documentation or more detailed information about the data kind.
     */
    private String docs;

    /**
     * The serialized data type of this kind.
     * <br> This specifies how the data should be serialized for transport or storage (e.g., JSON, XML).
     */
    private String serializedDataType;

    /**
     * The internal data type of this kind.
     * <br> This represents how the data is stored or processed internally (e.g., integer, float, string).
     */
    private String internalDataType;
}
