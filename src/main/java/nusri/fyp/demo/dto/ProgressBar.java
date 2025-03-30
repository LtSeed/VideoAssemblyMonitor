package nusri.fyp.demo.dto;

import lombok.Getter;
import lombok.Setter;
import nusri.fyp.demo.service.ConfigService;
import nusri.fyp.demo.state_machine.Node;

import java.util.List;
import java.util.Objects;

/**
 * Represents progress information for a node, including index, name, quota, parent node index list, and current progress percentage.
 * <br>
 * Typically used for frontend display or API responses to visualize the execution progress of state machine nodes.
 */
@Getter
@Setter
public class ProgressBar {
    /**
     * The index of the node, corresponding to the node ID in the state machine.
     */
    private int index;

    private String name;

    private double quota;

    private List<Integer> parent;

    private double progress;

    private double p;


    /**
     * Constructs a ProgressBar from a {@link Node}, using the provided configuration service and preset name.
     *
     * @param node           The state machine node.
     * @param configService  The configuration service.
     * @param presetName     The name of the preset.
     */
    public ProgressBar(Node node, ConfigService configService, String presetName) {
        this.index = node.getId();
        this.name = node.getName();
        this.quota = node.getCalculateQuota(configService, presetName);
        this.parent = node.getParents().stream().map(Node::getId).toList();
        this.progress = node.T_divideByQuota();
        this.p = node.P();
    }

    /**
     * Returns a string representation of the progress bar, including node index, name, quota, parents, progress, and p value.
     *
     * @return The string.
     */
    @Override
    public String toString() {
        return "ProgressBar{" +
                "index=" + index +
                ", name='" + name + '\'' +
                ", quota=" + quota +
                ", parent=" + String.join(", ", parent.stream().map(Objects::toString).toList()) +
                ", progress=" + progress +
                ", p=" + p +
                '}';
    }
}