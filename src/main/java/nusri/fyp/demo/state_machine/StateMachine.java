package nusri.fyp.demo.state_machine;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nusri.fyp.demo.entity.Preset;
import nusri.fyp.demo.entity.PresetNode;
import nusri.fyp.demo.entity.QuotaConfig;
import nusri.fyp.demo.roboflow.data.entity.workflow.SinglePrediction;
import nusri.fyp.demo.service.ConfigService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * <p>
 * StateMachine maintains a state graph based on {@link Node} and performs state updates based on different
 * action-object observations. When Quota is enabled, it uses the traditional logic; when Quota is disabled,
 * it skips.
 * </p>
 *
 * Main Methods:
 * <ul>
 *   <li>{@link #updateStateProbability(List, double, ConfigService)}: Updates state in real-time when Quota is enabled;
 *       if Quota is disabled, it skips this step.</li>
 *   <li>{@code  #clearAndUpdateToTime(double, ConfigService)}: Clears state and recalculates up to a given time point;
 *       if Quota is disabled, it skips this step.</li>
 *   <li>{@link #getMostProbableState()}: Returns the current node with the highest probability.</li>
 * </ul>
 * @author Liu Binghong
 * @since 1.0
 */
@Slf4j
@Getter
public class StateMachine {

    /**
     * Debug switch for logging detailed information.
     */
    private final boolean DEBUG = true;

    /**
     * List of all nodes in the process graph (excluding Idle).
     */
    private final List<Node> nodes = new ArrayList<>();

    /**
     * Idle node representing the state when no valid action is available.
     */
    private final Node idle = new Node(0, 100, "Idle");

    /**
     * Timestamp (in seconds) of the last state update.
     */
    private double lastUpdate = 0;

    /**
     * A map holding all observations, with timestamps (ms) as keys and lists of corresponding action observations.
     */
    @Setter
    private Map<Long, List<AbstractActionObservation>> observations = new ConcurrentHashMap<>();

    /**
     * The {@link Preset} associated with this StateMachine, which contains configuration and process information.
     */
    private final Preset preset;

    /**
     * The time at which the StateMachine was started, used as a reference point.
     */
    private final LocalDateTime startTime;

    /**
     * Constructs a StateMachine based on the provided {@link Preset}.
     *
     * @param preset The preset containing the configuration for the state machine.
     */
    public StateMachine(Preset preset) {
        this.preset = preset;
        this.startTime = LocalDateTime.now();

        // Convert preset nodes to Node objects and add them to the nodes list
        if (preset.getNodes() != null) {
            nodes.addAll(preset.getNodes().stream().map(PresetNode::toNode).toList());
        }
    }

    /**
     * Retrieves all nodes that can receive the specified action.
     *
     * @param s The action-object string (e.g., "action-object").
     * @return A list of nodes that can process the given action.
     */
    public List<Node> getNodesByS(String s) {
        return nodes.stream().filter(node -> node.canReceiveAction(s)).distinct().toList();
    }

    /**
     * Finds the predecessor nodes for a given node (n) and action (s).
     *
     * @param n The target node.
     * @param s The action-object string.
     * @return A list of nodes that can be predecessors of the given node for the specified action.
     */
    public List<Node> getNpByS(Node n, String s) {
        return getNodesByS(s).stream().filter(n::isPreprocess).toList();
    }

    /**
     * Updates the state probabilities and accumulates the work time for each node based on action observations.
     * This method works when Quota is enabled. If Quota is disabled, the method will skip the real-time update.
     *
     * @param obs The list of action observations at the current timestamp.
     * @param timestamp The current time (in seconds).
     * @param configService The configuration service used to access Quota settings.
     */
    public void updateStateProbability(List<AbstractActionObservation> obs,
                                       double timestamp,
                                       ConfigService configService) {
        obs = new ArrayList<>(obs);
        boolean isQuotaDisabled = isDisabled(configService, this.getPreset().getName());


        if (!isQuotaDisabled) {
            QuotaConfig quotaConfig = configService.getQuotaConfig(this.getPreset().getName());
            final double w = (timestamp - lastUpdate) / 1000;
            // 过大或为0则直接return.
            lastUpdate = timestamp;
            if (w == 0 || w > 100000) {

                log.info("jump because of w out of range: {}", w);
                return;
            }


            clearP();

            if (obs.isEmpty()) {
                ActionObservation actionObservation = new ActionObservation();
                actionObservation.setProbability(1);
                actionObservation.setActionAndObject("transfer");

                obs.add(actionObservation);
            }

            List<? extends AbstractActionObservation> observations = new ArrayList<>(obs);
            observations.sort(Comparator.comparingDouble(AbstractActionObservation::getProbability).reversed());
            List<Node> changedNodes = new ArrayList<>();
            if (observations.size() >= 2 && observations.get(0) instanceof SinglePrediction && ((SinglePrediction) observations.get(0)).getLabel() != null) {
                List<SinglePrediction> actions = observations.stream()
                        .map(a -> (SinglePrediction) a)
                        .filter(s -> s.getLabel().startsWith("action"))
                        .toList();
                List<SinglePrediction> objects = observations.stream()
                        .map(a -> (SinglePrediction) a)
                        .filter(s -> s.getLabel().startsWith("object"))
                        .toList();
                List<AbstractActionObservation> newList = new ArrayList<>();
                for (SinglePrediction action : actions) {
                    for (SinglePrediction object : objects) {
                        SinglePrediction e = new SinglePrediction(action, object);
                        newList.add(e);

                    }
                }
                observations = newList;
                log.info("Observations of Prediction is of SinglePrediction: new List: {}", newList);

            }
            double sum = observations.stream().mapToDouble(AbstractActionObservation::getProbability).sum();

            if (DEBUG && sum != 0) {
                observations.forEach(o->o.setProbability(o.getProbability() / sum));
            }

            for (AbstractActionObservation observation : observations) {
                List<Node> nodesByS = getNodesByS(observation.s());
                if (nodesByS.size() > 1 && !nodesByS.stream().allMatch(o->o.C() >= 1)) {
                    changedNodes.addAll(nodesByS.stream().filter(o->o.C() < 1).toList());
                } else {
                    changedNodes.addAll(nodesByS);
                }
                int Ks = changedNodes.size();
                changedNodes.forEach(node -> node.addFirstAllocation(observation.s(), observation.getProbability() / Ks));
            }
            idle.setProbability(1 - observations.stream().mapToDouble(AbstractActionObservation::getProbability).sum());
            for (Node changedNode : changedNodes) {
                for (Map.Entry<String, Double> sAndV : changedNode.firstAllocation.entrySet()) {
                    List<Node> npByS = getNpByS(changedNode, sAndV.getKey());
                    double remain = sAndV.getValue();
                    double fnCount = npByS.stream().mapToDouble(node->node.F(quotaConfig)).sum();
                    int stack = 0;
                    if (fnCount != 0) while (remain > 0.01) {
                        stack++;
                        double final_remain = remain;
                        int fullCount = 0;
                        for (Node node : npByS) {
                            double a = final_remain * (node.F(quotaConfig) / fnCount);
                            double b = (node.getCalculateQuota(configService, this.getPreset().getName()) - node.T(configService, this.getPreset().getName())) / w - node.P();
                            double dp = max(min(a, b), 0);
                            fullCount = (dp == b || (dp <= 0.01))? 1: 0;
                            remain -= dp;
                            node.addP(dp);
                        }
                        if (fullCount == npByS.size() || stack > 5) {
                            break;
                        }
                    }
                    if (remain < 0.001) { remain = 0.0; }
                    changedNode.addP(remain);
                }
            }
            for (Node node : nodes) {
                if (node.P() <= 0.01) node.setProbability(0.0);
                else if (node.P() >= 1) node.setProbability(1.0);
            }
            log.debug("Probabilities of Predictions is {}", nodes.stream().map(n -> String.valueOf(n.P())).collect(Collectors.joining(", ")));
            nodes.forEach(node -> node.applyTime(w, configService, this.getPreset().getName()));
            idle.applyTime(w, configService, this.getPreset().getName());
            log.debug("Visit P of Predictions is {}", nodes.stream().map(n -> String.valueOf(n.realC())).collect(Collectors.joining(", ")));
            log.debug("D of Predictions is {}", nodes.stream().map(n -> String.valueOf(n.D(quotaConfig))).collect(Collectors.joining(", ")));
            log.debug("E of Predictions is {}", nodes.stream().map(n -> String.valueOf(n.E(quotaConfig))).collect(Collectors.joining(", ")));
            log.debug("UQ of Predictions is {}", nodes.stream().map(n -> String.valueOf(n.getUpperQuota(quotaConfig))).collect(Collectors.joining(", ")));
            log.debug("LQ of Predictions is {}", nodes.stream().map(n -> String.valueOf(n.getLowerQuota(quotaConfig))).collect(Collectors.joining(", ")));

        } else {
            log.debug("Quota disabled -> Skip real-time update. Will do HMM/Viterbi offline.");
        }
    }

    /**
     * Resets the states of all nodes and recalculates up to the specified time point.<br>
     * If Quota is enabled, the state update occurs using the traditional method; <br>
     * if Quota is disabled, it skips. <br>
     *
     * @param tillTimestamp    The timestamp (in seconds) up to which the state is recalculated. This is relative to
     *                         {@link #startTime}.
     * @param configService    The service that provides configuration information.
     * @param actions          The list of possible actions for the current operation.
     * @param objects          The list of possible objects for the current operation.
     */
    public void clearAndUpdateToTime(double tillTimestamp, ConfigService configService, List<String> actions, List<String> objects) {

        log.info("clearAndUpdateToTime, observation size: {}", observations.size());

        nodes.forEach(Node::clear);
        idle.clear();
        lastUpdate = 0;

        long startSec = startTime.toEpochSecond(ZoneOffset.of("+8"));

        boolean isQuotaDisabled = isDisabled(configService, this.getPreset().getName());

        if (!isQuotaDisabled) {
            observations.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey(Comparator.comparing(k -> k)))
                    .filter(e -> ((e.getKey() > startSec) ? (e.getKey() - startSec) : e.getKey()) <= tillTimestamp * 1000)
                    .forEach(e -> this.updateStateProbability(e.getValue(), e.getKey(), configService));

        }
    }

    /**
     * A cache to store the status of whether Quota is disabled for a given preset.
     */
    public static final Map<String, Boolean> cache = new HashMap<>();

    /**
     * Checks if the Quota for the given preset is disabled by consulting the configuration service.
     * The result is cached for efficiency.
     *
     * @param configService The configuration service to access Quota settings.
     * @param presetName    The name of the preset to check.
     * @return {@code true} if Quota is disabled for the specified preset, {@code false} otherwise.
     */
    public static boolean isDisabled(ConfigService configService, String presetName) {
        if (cache.containsKey(presetName)) {
            return cache.get(presetName);
        } else {
            boolean disabled = configService.getQuotaConfig(presetName)
                    .getQuotaMode()
                    .equalsIgnoreCase("disabled");
            cache.put(presetName, disabled);
            return disabled;
        }
    }

    /**
     * Clears all node probabilities and first allocation information, but retains the visit probability.
     * If you want to clear the visit probability as well, you can do so by calling {@link Node#clear()}.
     */
    private void clearP() {
        idle.setProbability(0.0);
        nodes.forEach(n -> {
            n.setProbability(0.0);
            n.setFirstAllocation(new HashMap<>());
        });
    }

    /**
     * Returns the {@link PresetNode} object corresponding to the current node with the highest probability.
     * If all nodes have no probability, it returns the {@link PresetNode} corresponding to the Idle node (if it matches).
     *
     * @return The {@link PresetNode} corresponding to the node with the highest probability.
     */
    public PresetNode getMostProbableState() {
        Node bestNode = nodes.stream()
                .max(Comparator.comparingDouble(Node::P))
                .orElse(idle);
        return PresetNode.getPresetNode(preset, bestNode);
    }
}
