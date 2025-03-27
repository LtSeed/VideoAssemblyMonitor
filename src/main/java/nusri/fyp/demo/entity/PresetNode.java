package nusri.fyp.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import nusri.fyp.demo.state_machine.Node;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * PresetNode entity representing a single step in a preset workflow.
 * <br> It uses {@link PresetNodeId} as a composite key and links to the specific {@link Preset}.
 * It also contains node information such as name, quota, parent nodes, and triggerable actions.
 */
@Entity
@Getter
@Setter
@Table(name = "preset_node")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PresetNode {

    /**
     * A cache storing the relationship between nodes and their corresponding {@link PresetNode}.
     * The global cache helps to reduce the creation of duplicate objects.
     */
    @JsonIgnore
    public static final Map<Node, PresetNode> PRESET_MAP = new ConcurrentHashMap<>();

    /**
     * Retrieves or creates a {@link PresetNode} object, associating it with the given preset and node.
     *
     * @param preset The target preset.
     * @param node   The node from the state machine.
     * @return The matching or newly created {@link PresetNode} instance.
     */
    @JsonIgnore
    public static PresetNode getPresetNode(Preset preset, Node node) {
        PresetNode orDefault = PRESET_MAP.getOrDefault(node, new PresetNode(preset, node));
        PRESET_MAP.put(node, orDefault);
        return orDefault;
    }

    /**
     * Converts the given {@link PresetNode} to a corresponding {@link Node} in the state machine.
     *
     * @param presetNode The preset node entity.
     * @return The corresponding state machine {@link Node}.
     */
    @JsonIgnore
    public static Node getNode(PresetNode presetNode) {
        Node node = new Node(presetNode.id.getNumber(),
                presetNode.realQuota,
                presetNode.name,
                presetNode.actions,
                presetNode.parents.stream()
                        .map(PresetNode::getNode)
                        .collect(Collectors.toSet()));
        Map.Entry<Node, PresetNode> entry = PRESET_MAP.entrySet().stream()
                .filter(o -> o.getValue().equals(presetNode))
                .findFirst()
                .orElse(new AbstractMap.SimpleEntry<>(node, null));
        if (entry.getValue() == null) {
            PRESET_MAP.put(node, presetNode);
        }
        return entry.getKey();
    }

    /**
     * The name of the node, such as "OpenDoorStep".
     */
    private String name;

    /**
     * The composite primary key, which includes the {@link Preset} and the node number.
     */
    @EmbeddedId
    private PresetNodeId id;  // Composite key

    /**
     * The actual quota used in process control as a threshold or reference.
     */
    private double realQuota;

    /**
     * The order of the node in the workflow. This field is optional and depends on the business logic.
     */
    private int rank;

    /**
     * A set of parent nodes, representing the previous step(s) that this node depends on.
     * <br> A many-to-many relationship is maintained in the database via the intermediate table {@code preset_node_parents}.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "preset_node_parents",
            joinColumns = {
                    @JoinColumn(name = "preset_node_id_preset_id", referencedColumnName = "preset_id"),
                    @JoinColumn(name = "preset_node_id_number", referencedColumnName = "number")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "parent_node_id_preset_id", referencedColumnName = "preset_id"),
                    @JoinColumn(name = "parent_node_id_number", referencedColumnName = "number")
            }
    )
    private Set<PresetNode> parents;  // Set of parent nodes

    /**
     * A list of actions that can be triggered by this node, such as ["open", "close"].
     */
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> actions;

    /**
     * No-arg constructor required by JPA or deserialization.
     */
    public PresetNode() {
    }

    /**
     * A private helper constructor for initializing a {@link PresetNode} from a {@link Node}.
     *
     * @param preset The associated preset.
     * @param node   The corresponding state machine node.
     */
    private PresetNode(Preset preset, Node node) {
        this.name = node.getName();
        this.id = new PresetNodeId(preset, node.getId());
        this.realQuota = node.getRealQuota();
        this.id.setPreset(preset);
        this.parents = node.getParents().stream()
                .map(n -> PresetNode.getPresetNode(preset, n))
                .collect(Collectors.toSet());
        this.actions = node.getActions();
        List<PresetNode> nodes = preset.getNodes();
        nodes.add(this);
        preset.setNodes(nodes);
    }

    /**
     * Converts this {@link PresetNode} to a corresponding {@link Node} in the state machine.
     *
     * @return The converted {@link Node}.
     */
    public Node toNode() {
        return getNode(this);
    }

    /**
     * Object debug output displaying the main fields.
     *
     * @return A string representation.
     */
    @Override
    public String toString() {
        return "PN(" +
                id +
                ')';
    }
}
