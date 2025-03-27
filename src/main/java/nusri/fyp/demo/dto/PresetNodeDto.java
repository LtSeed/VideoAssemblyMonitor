package nusri.fyp.demo.dto;

import lombok.Getter;
import lombok.Setter;
import nusri.fyp.demo.entity.PresetNode;

import java.util.*;
import java.util.stream.Collectors;

/**
 * DTO corresponding to the {@link nusri.fyp.demo.entity.PresetNode}.
 * <br>
 * Contains node name, composite primary key {@link PresetNodeIdDto}, quota, rank, and parent nodes along with triggerable actions.
 */
@Getter
@Setter
public class PresetNodeDto {
    /**
     * The name of the node, e.g., "OpenDoorStep".
     */
    private String name;

    /**
     * The DTO corresponding to the composite primary key.
     */
    private PresetNodeIdDto id;  // Composite primary key

    /**
     * The actual quota value.
     */
    private double realQuota;

    /**
     * The rank or order of the node (optional).
     */
    private int rank;

    /**
     * A set of parent nodes in DTO form.
     */
    private Set<PresetNodeDto> parents;  // Set of parent nodes

    /**
     * A list of actions that can be triggered by the node.
     */
    private List<String> actions;

    /**
     * Constructs a DTO from the {@link nusri.fyp.demo.entity.PresetNode} entity.
     *
     * @param presetNode The preset node entity.
     */
    public PresetNodeDto(PresetNode presetNode) {
        this.name = presetNode.getName();
        this.id = new PresetNodeIdDto(presetNode.getId());
        this.realQuota = presetNode.getRealQuota();
        this.rank = presetNode.getRank();
        this.parents = presetNode.getParents().stream().map(PresetNodeDto::new).collect(Collectors.toSet());
        this.actions = presetNode.getActions();
    }

    /**
     * Prints debug information showing the main fields.
     *
     * @return The string representation.
     */
    @Override
    public String toString() {
        return "PresetNodeDto{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}