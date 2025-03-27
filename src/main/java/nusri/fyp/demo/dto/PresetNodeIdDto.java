package nusri.fyp.demo.dto;

import lombok.Getter;
import lombok.Setter;
import nusri.fyp.demo.entity.PresetNodeId;

/**
 * DTO corresponding to the composite primary key of a preset node.
 * <br>
 * Contains the preset ID and node number (e.g., step sequence number in a workflow).
 */
@Getter
@Setter
public class PresetNodeIdDto {
    /**
     * The ID of the associated preset.
     */
    private Long preset;  // Associated Preset entity ID

    /**
     * The node number, such as the step sequence in the workflow.
     */
    private int number;  // Other attributes

    /**
     * Constructs a DTO from the composite primary key entity.
     *
     * @param presetNodeId The composite primary key entity.
     */
    PresetNodeIdDto(PresetNodeId presetNodeId) {
        preset = presetNodeId.getPreset().getId();
        number = presetNodeId.getNumber();
    }

    /**
     * Returns a string representation, showing the node number.
     *
     * @return The string.
     */
    @Override
    public String toString() {
        return "PresetNodeIdDto{" +
                "number=" + number +
                '}';
    }
}