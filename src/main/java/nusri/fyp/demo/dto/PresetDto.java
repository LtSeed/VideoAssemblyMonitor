package nusri.fyp.demo.dto;

import lombok.Getter;
import lombok.Setter;
import nusri.fyp.demo.entity.Preset;

import java.util.List;

/**
 * A DTO for preset data, used for returning simplified preset information at the interface or service layer.
 * <br>
 * Contains preset ID, name, and a list of node information ({@link PresetNodeDto}).
 */
@Getter
@Setter
public class PresetDto {
    /**
     * The unique ID of the preset.
     */
    private Long id;

    /**
     * The name of the preset.
     */
    private String name;

    /**
     * A list of nodes in DTO form.
     */
    private List<PresetNodeDto> nodes;

    /**
     * Constructs a DTO from the {@link nusri.fyp.demo.entity.Preset} entity.
     *
     * @param preset The preset entity.
     */
    public PresetDto(Preset preset) {
        this.id = preset.getId();
        this.name = preset.getName();
        this.nodes = preset.getNodes().stream().map(PresetNodeDto::new).toList();
    }

    /**
     * Overrides the {@code toString()} method to include the preset ID and name.
     *
     * @return The concatenated string.
     */
    @Override
    public String toString() {
        return "PresetDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
