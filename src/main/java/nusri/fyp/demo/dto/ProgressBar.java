package nusri.fyp.demo.dto;

import lombok.Getter;
import lombok.Setter;
import nusri.fyp.demo.service.ConfigService;
import nusri.fyp.demo.state_machine.Node;

import java.util.List;
import java.util.Objects;

/**
 * Represents progress information for a node within a state machine.
 * <p>
 * This class encapsulates key details such as the node's index, name, quota, parent node indices,
 * current progress, and an additional computed parameter 'p'. Instances of this class are typically used
 * for frontend display or API responses to visualize the execution progress of state machine nodes.
 * </p>
 */
@Getter
@Setter
public class ProgressBar {
    /**
     * The index of the node, corresponding to the node ID in the state machine.
     */
    private int index;

    /**
     * The name of the node.
     */
    private String name;

    /**
     * The quota assigned to the node, representing the total capacity or target value used in progress calculation.
     */
    private double quota;

    /**
     * A list of parent node indices. This list represents the node IDs of the direct ancestors in the state machine.
     */
    private List<Integer> parent;

    /**
     * The current progress of the node, computed as a fraction or percentage of the quota that has been completed.
     */
    private double progress;

    /**
     * A computed parameter 'p' related to the node's progress or performance.
     * The exact meaning of this parameter is determined by the state machine logic.
     */
    private double p;

    /**
     * Constructs a {@code ProgressBar} from a given {@link Node}.
     * <p>
     * This constructor initializes the progress bar by extracting relevant details from the provided node.
     * It calculates the quota using the provided configuration service and preset name, retrieves the list of parent node IDs,
     * and computes the current progress and parameter 'p' using the node's internal methods.
     * </p>
     *
     * @param node           The state machine node from which to extract progress information.
     * @param configService  The configuration service used to compute the node's quota.
     * @param presetName     The name of the preset configuration used in quota calculation.
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
     * Returns a string representation of this progress bar.
     * <p>
     * The string includes the node's index, name, quota, list of parent node indices,
     * progress value, and the computed parameter 'p'.
     * </p>
     *
     * @return A string describing this progress bar.
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
