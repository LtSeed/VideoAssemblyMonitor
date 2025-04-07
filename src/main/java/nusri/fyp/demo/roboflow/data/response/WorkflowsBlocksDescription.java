package nusri.fyp.demo.roboflow.data.response;

import lombok.Data;
import nusri.fyp.demo.roboflow.data.AnyData;
import nusri.fyp.demo.roboflow.data.RoboflowResponseData;
import nusri.fyp.demo.roboflow.data.entity.*;

import java.util.List;

/**
 * Represents a description of blocks within workflows in the Roboflow API.
 * <br> This class provides detailed information about the blocks, their connections, and the associated properties and schema.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@Data
public class WorkflowsBlocksDescription implements RoboflowResponseData {

    /**
     * A list of {@link BlockDescription} objects representing the description of blocks in the workflow.
     * <br> Each block defines a specific task or operation within the workflow.
     */
    private List<BlockDescription> blocks;

    /**
     * A list of {@link Kind} objects that represent the declared kinds within the workflow.
     * <br> These kinds define the data structures or types used in the workflow.
     */
    private List<Kind> declaredKinds;

    /**
     * An {@link AnyData} object that holds the connections between kinds in the workflow.
     * <br> This field provides a representation of how different kinds are connected in the workflow.
     */
    private AnyData kindsConnections;

    /**
     * A list of {@link ExternalBlockPropertyPrimitiveDefinition} objects representing the connections to external block properties.
     * <br> These define the primitive properties and their connections in the workflow.
     */
    private List<ExternalBlockPropertyPrimitiveDefinition> primitivesConnections;

    /**
     * A {@link UniversalQueryLanguageDescription} object that describes the universal query language used in the workflow.
     * <br> This field provides details about the query language that can be used to interact with the workflow blocks.
     */
    private UniversalQueryLanguageDescription universalQueryLanguageDescription;

    /**
     * An {@link AnyData} object that represents the dynamic block definition schema for the workflow.
     * <br> This field defines the schema for dynamic blocks within the workflow.
     */
    private AnyData dynamicBlockDefinitionSchema;
}
