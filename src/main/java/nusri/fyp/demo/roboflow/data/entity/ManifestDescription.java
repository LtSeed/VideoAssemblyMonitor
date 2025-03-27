package nusri.fyp.demo.roboflow.data.entity;

import lombok.Data;
import nusri.fyp.demo.roboflow.data.AnyData;
import java.util.List;

/**
 * Describes a manifest, including input/output types and additional parameters.
 * <br> This class defines the structure of a manifest in the context of workflows or blocks,
 * providing detailed information about the types of inputs, outputs, and various properties
 * such as batch processing capabilities and scalar handling.
 */
@Data
public class ManifestDescription {

    /**
     * The type of the manifest.
     * <br> This can represent the category or classification of the manifest (e.g., "model", "block").
     */
    private String type;

    /**
     * The type of the block associated with the manifest.
     * <br> This defines the specific kind of block (e.g., "image processing", "text processing").
     */
    private String blockType;

    /**
     * A description of the manifest's purpose or functionality.
     * <br> This provides a more detailed explanation of what the manifest represents or how it should be used.
     */
    private String description;

    /**
     * The inputs for the manifest, which could include various data parameters.
     * <br> This field can be further defined to represent specific input types, depending on the manifest's usage.
     */
    private AnyData inputs;

    /**
     * The outputs of the manifest, which could include processed results or transformations.
     * <br> Similar to inputs, this can be more specifically defined based on the manifest's structure and purpose.
     */
    private AnyData outputs;

    /**
     * The dimensionality offset for the output.
     * <br> This field is used to represent any shift or transformation in the output's dimensionality.
     */
    private Integer outputDimensionalityOffset;

    /**
     * Indicates whether the manifest accepts batch input.
     * <br> If true, the manifest can handle input data in batches.
     */
    private Boolean acceptsBatchInput;

    /**
     * Specifies whether the manifest accepts empty values.
     * <br> This field indicates whether missing or null values are valid inputs for the manifest.
     */
    private Boolean acceptsEmptyValues;

    /**
     * A list of parameters that are oriented towards batch processing.
     * <br> This field lists any parameters that are specifically designed to handle batch inputs or outputs.
     */
    private List<String> batchOrientedParameters;

    /**
     * A list of parameters that can handle both scalar and batch values.
     * <br> This field defines parameters that can process both individual values (scalars) and collections (batches).
     */
    private List<String> parametersWithScalarsAndBatches;
}
