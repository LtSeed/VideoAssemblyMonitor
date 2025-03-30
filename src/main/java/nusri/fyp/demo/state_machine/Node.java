package nusri.fyp.demo.state_machine;

import lombok.Getter;
import lombok.Setter;
import nusri.fyp.demo.entity.QuotaConfig;
import nusri.fyp.demo.entity.SingleQuota;
import nusri.fyp.demo.entity.SingleQuotaOfOffset;
import nusri.fyp.demo.service.ConfigService;

import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Represents a node in a state machine process.
 * <br> This class handles the management of a node's actions, probabilities, and quotas, as well as interactions with parent nodes.
 */
public class Node {
    /**
     * The name of the node.
     */
    @Getter
    private final String name;

    /**
     * The unique identifier of the node.
     */
    @Getter
    private final int id;

    /**
     * The probability of visiting the node.
     * <br> This represents how likely the node is to be traversed in the state machine.
     */
    private double visitProbability;

    /**
     * The real quota associated with the node.
     * <br> This represents the actual workload capacity or limit of the node.
     */
    @Getter
    private final double realQuota;

    /**
     * The current probability value (P(N)) for the node.
     * <br> This represents the probability of the node being selected or processed in the state machine.
     */
    @Setter
    private double probability;

    /**
     * A set of parent nodes for the current node.
     * <br> These represent the nodes that must be processed before this node can be activated.
     */
    @Getter
    private final Set<Node> parents;

    /**
     * A list of actions associated with the node.
     * <br> These actions define what operations or transitions are linked to the node.
     */
    @Getter
    private final List<String> actions;

    /**
     * A map holding the first allocation probabilities for various actions.
     * <br> This represents the initial probabilities assigned to different actions for the node.
     */
    @Getter
    @Setter
    Map<String, Double> firstAllocation = new HashMap<>();

    /**
     * Constructs a Node with a specified ID, quota, and name, initializing with an empty actions list and no parent nodes.
     *
     * @param id        The unique identifier for the node.
     * @param realQuota The actual quota associated with the node.
     * @param name      The name of the node.
     */
    public Node(int id, double realQuota, String name) {
        this.name = name;
        ArrayList<String> actions1 = new ArrayList<>();
        actions1.add(name);
        this.actions = actions1;
        this.id = id;
        this.realQuota = realQuota;
        this.probability = 0.0;
        this.visitProbability = 0.0;
        this.parents = new HashSet<>();
    }

    /**
     * Constructs a Node with a specified ID, quota, name, actions, and parent nodes.
     *
     * @param id        The unique identifier for the node.
     * @param realQuota The actual quota associated with the node.
     * @param name      The name of the node.
     * @param actions   The list of actions associated with the node.
     * @param parents   The set of parent nodes for the node.
     */
    public Node(int id, double realQuota, String name, List<String> actions, Set<Node> parents) {
        this.name = name;
        this.actions = actions;
        this.id = id;
        this.realQuota = realQuota;
        this.probability = 0.0;
        this.visitProbability = 0.0;
        this.parents = parents;
    }

    /**
     * Gives a first allocation to this node.
     *
     * @param s The action to check.
     * @param probability the probability the action give.
     */
    public void addFirstAllocation(String s, double probability) {
        firstAllocation.put(s, probability);
    }

    /**
     * Checks if the node can receive a specified action.
     *
     * @param action The action to check.
     * @return True if the node can accept the action; false otherwise.
     */
    public boolean canReceiveAction(String action) {
        return actions.stream().anyMatch(a -> a.equalsIgnoreCase(action) || a.startsWith(action) || a.endsWith(action));
    }

    /**
     * Returns the probability of the node.
     *
     * @return The probability value (P(N)) for the node.
     */
    public double P() {
        return probability;
    }

    /**
     * Adds a probability value to the node's probability.
     *
     * @param probability The probability to add.
     */
    public void addP(double probability) {
        this.probability += probability;
    }

    /**
     * Computes the completion degree function (F) for the node, which is the product of the completion degrees of the parent nodes.
     *
     * @param quotaConfig The quota configuration to use for computation.
     * @return The product of the completion degrees of the parent nodes.
     */
    public double F(QuotaConfig quotaConfig) {
        return parents.stream().mapToDouble(o -> o.isDone(quotaConfig) ? 1 : o.C()).reduce((a, b) -> a * b).orElse(1);
    }

    /**
     * Checks if the node has completed its processing based on the quota configuration.
     *
     * @param quotaConfig The quota configuration to use for completion check.
     * @return True if the node has completed its processing; false otherwise.
     */
    public boolean isDone(QuotaConfig quotaConfig) {
        return realC() * getCalculateQuota(quotaConfig) >= getLowerQuota(quotaConfig);
    }

    /**
     * Checks if the node has exceeded its time limit based on the quota configuration.
     *
     * @param quotaConfig The quota configuration to use for timeout check.
     * @return True if the node has exceeded its time limit; false otherwise.
     */
    public boolean isTimeout(QuotaConfig quotaConfig) {
        return realC() * getCalculateQuota(quotaConfig) >= getUpperQuota(quotaConfig);
    }

    /**
     * Returns the completion ratio of the node, which is in [0, 1].
     *
     * @return The completion ratio of the node.
     */
    public double C() {
        return min(1, this.visitProbability);
    }

    /**
     * Returns the actual completion ratio of the node.
     *
     * @return The actual completion ratio.
     */
    public double realC() {
        return this.visitProbability;
    }

    /**
     * Checks if a given node is a predecessor of the current node.
     *
     * @param n The node to check.
     * @return True if the given node is a predecessor; false otherwise.
     */
    public boolean isPreprocess(Node n) {
        if (parents.isEmpty()) return false;
        if (parents.contains(n)) return true;
        return parents.stream().anyMatch(p -> p.isPreprocess(n));
    }

    /**
     * Calculates the elapsed time for the node, based on the provided quota.
     *
     * @param quota The quota to use for the calculation.
     * @return The accumulated elapsed time for the node.
     */
    public double T(SingleQuota quota) {
        return this.visitProbability * Double.parseDouble(quota.getQuota());
    }

    /**
     * Calculates the elapsed time for the node, based on the quota configuration and preset name.
     *
     * @param configService The config service to retrieve the quota configuration.
     * @param presetName    The preset name to use for fetching the quota configuration.
     * @return The accumulated elapsed time for the node.
     */
    public double T(ConfigService configService, String presetName) {
        return this.visitProbability * getCalculateQuota(configService, presetName);
    }

    /**
     * Calculates the timeout probability for the node.
     *
     * @param quotaConfig The quota configuration to use for the calculation.
     * @return The timeout probability for the node.
     */
    public double D(QuotaConfig quotaConfig) {
        return isTimeout(quotaConfig) ? max(0, this.visitProbability - 1) : 0;
    }

    /**
     * Calculates the error probability for the node.
     *
     * @param quotaConfig The quota configuration to use for the calculation.
     * @return The error probability for the node.
     */
    public double E(QuotaConfig quotaConfig) {
        return C() * (1 - F(quotaConfig));
    }

    /**
     * Applies a given time weight to the node's visit probability, based on the quota configuration and preset name.
     *
     * @param w             The time weight to apply.
     * @param configService The config service to retrieve the quota configuration.
     * @param presetName    The preset name to use for fetching the quota configuration.
     */
    public void applyTime(double w, ConfigService configService, String presetName) {
        this.visitProbability += this.probability * max(w, 0) / getCalculateQuota(configService, presetName);
    }

    /**
     * Clears the probability and visit probability of the node.
     */
    public void clear() {
        this.probability = 0.0;
        this.visitProbability = 0.0;
    }

    /**
     * Calculates the elapsed time divided by the quota.
     *
     * @return The ratio of the elapsed time to the quota.
     */
    public double T_divideByQuota() {
        return this.visitProbability;
    }

    /**
     * Calculates the quota for the node based on the config service and preset name.
     *
     * @param configService The config service to retrieve the quota configuration.
     * @param presetName    The preset name to use for fetching the quota configuration.
     * @return The calculated quota for the node.
     */
    public double getCalculateQuota(ConfigService configService, String presetName) {
        QuotaConfig quotaConfig = configService.getQuotaConfig(presetName);
        return getCalculateQuota(quotaConfig);
    }

    /**
     * Calculates the quota for the node based on the quota configuration.
     *
     * @param quotaConfig The quota configuration to use for the calculation.
     * @return The calculated quota for the node.
     */
    public double getCalculateQuota(QuotaConfig quotaConfig) {
        if (this.name.equalsIgnoreCase("idle")) return 1000.0;
        if (quotaConfig.getQuotaMode().equalsIgnoreCase("disabled")) return getRealQuota();
        SingleQuotaOfOffset other = new SingleQuotaOfOffset(this.name, getRealQuota());
        SingleQuota singleQuota = quotaConfig.getQuotas().stream()
                .filter(quota -> quota.getProc().equals(this.name))
                .findAny()
                .orElse(other);
        return singleQuota.getQuota() == null ? (Double.parseDouble(singleQuota.getUpBoundary()) + Double.parseDouble(singleQuota.getDownBoundary())) / 2 : Double.parseDouble(singleQuota.getQuota());
    }

    /**
     * Retrieves the lower quota value for the node from the quota configuration.
     *
     * @param quotaConfig The quota configuration to use for the calculation.
     * @return The lower quota value for the node.
     */
    public double getLowerQuota(QuotaConfig quotaConfig) {
        if (this.name.equalsIgnoreCase("idle")) return 1000.0;
        if (quotaConfig.getQuotaMode().equalsIgnoreCase("disabled")) return getRealQuota();
        SingleQuotaOfOffset other = new SingleQuotaOfOffset(this.name, getRealQuota());
        return Double.parseDouble(quotaConfig.getQuotas().stream()
                .filter(quota -> quota.getProc().equals(this.name))
                .findAny()
                .orElse(other)
                .getDownBoundary()) / 1000;
    }

    /**
     * Retrieves the upper quota value for the node from the quota configuration.
     *
     * @param quotaConfig The quota configuration to use for the calculation.
     * @return The upper quota value for the node.
     */
    public double getUpperQuota(QuotaConfig quotaConfig) {
        if (this.name.equalsIgnoreCase("idle")) return 1000.0;
        if (quotaConfig.getQuotaMode().equalsIgnoreCase("disabled")) return getRealQuota();
        SingleQuotaOfOffset other = new SingleQuotaOfOffset(this.name, getRealQuota());
        return Double.parseDouble(quotaConfig.getQuotas().stream()
                .filter(quota -> quota.getProc().equals(this.name))
                .findAny()
                .orElse(other)
                .getUpBoundary()) / 1000;
    }

    /**
     * Get if the node is handling node. A handling node means this node will not add anything to the
     * assembly while its action only contains "transfer".
     *
     * @return true if the node is handling node, false otherwise.
     */
    public boolean isHandlingNode() {
        return this.getActions().contains("transfer");
    }
}
