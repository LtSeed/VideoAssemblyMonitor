package nusri.fyp.demo.state_machine;

import java.util.*;

/**
 * The `SegmentPartitionByDP` class implements a dynamic programming solution to find an optimal partition
 * of data into multiple segments, considering various constraints such as quotas and processing time.
 * This class is designed to handle the segmentation of data into balanced partitions with the goal of
 * maximizing the overall score for each partition.
 * @author Liu Binghong
 * @since 1.0
 */
public class SegmentPartitionByDP {

    /**
     * Finds the optimal partitions for the given data using dynamic programming.
     * The data consists of a map where keys represent sorted data points and values represent lists
     * of integers associated with each key. The goal is to find the partition points (boundaries)
     * that maximize the total score while keeping the segments as balanced as possible.
     * <br>
     * The algorithm creates a DP table to store the maximum score possible for each partition.
     * After filling the DP table, it reconstructs the optimal partition by backtracking through the table.
     * <br>
     * The final partition is chosen based on the maximum score and balance among the segments.
     * <br>
     *
     * @param data A map where the keys represent sorted data points, and the values are lists of integers
     *             associated with each key.
     * @return A list of `Long` values representing the boundaries where partitions are made.
     */
    public static List<Long> findOptimalPartitions(Map<Long, List<Integer>> data) {
        // Extract and sort the keys
        List<Long> keys = new ArrayList<>(data.keySet());
        Collections.sort(keys);
        int n = keys.size();
        if (n == 0) {
            return Collections.emptyList();  // no keys, no partitions
        }

        // Determine K = max value + 1
        int maxValue = Integer.MIN_VALUE;
        for (List<Integer> values : data.values()) {
            for (int v : values) {
                if (v > maxValue) {
                    maxValue = v;
                }
            }
        }
        int K = maxValue + 1;
        if (K <= 1) {
            // If K=1, no partitions needed (only one segment covers all keys)
            return Collections.emptyList();
        }

        assert n >= K;
        // Not enough keys to form K segments (each segment needs at least one key)
        // This situation is not explicitly handled by the problem statement.
        // We can either throw an exception or handle by putting as many single-key segments as possible.
        // Here we choose to still create segments until keys run out (remaining segments would be empty).

        // Prefix count arrays: count[value][i] = number of occurrences of 'value' in the first i keys
        int[][] count = new int[K][n + 1];
        // Initialize prefix counts
        for (int i = 1; i <= n; i++) {
            Long key = keys.get(i - 1);
            List<Integer> values = data.get(key);
            // carry over counts from previous prefix
            for (int v = 0; v < K; v++) {
                count[v][i] = count[v][i - 1];
            }
            // increment counts for this key's values
            if (values != null) {
                for (int v : values) {
                    if (v >= 0 && v < K) {
                        count[v][i] += 1;
                    }
                }
            }
        }

        // DP table: dp[seg][i] = max score using 'seg' segments to cover first i keys
        // Using 1-index for segments count (seg segments means indices 0..seg-1 used)
        double[][] dp = new double[K + 1][n + 1];  // use double for -inf convenience (or use Integer.MIN for impossible)
        for (int seg = 0; seg <= K; seg++) {
            Arrays.fill(dp[seg], Double.NEGATIVE_INFINITY);
        }
        dp[0][0] = 0.0;  // 0 segments cover 0 keys with 0 score
        // choice[seg][i] to store index where the last cut was made (end of previous segment)
        int[][] choiceIndex = new int[K + 1][n + 1];
        for (int seg = 1; seg <= K; seg++) {
            for (int i = 1; i <= n; i++) {
                // At least seg keys are needed to form seg segments (each segment gets >= 1 key)
                if (i < seg) {
                    dp[seg][i] = Double.NEGATIVE_INFINITY;
                    choiceIndex[seg][i] = -1;
                    continue;
                }
                int segIndex = seg - 1;  // segment index (0-based) for this seg count
                double bestScore = Double.NEGATIVE_INFINITY;
                int bestCut = -1;
                // Try ending the (seg-1)th segment at various earlier positions j
                for (int j = seg - 1; j < i; j++) {
                    if (dp[seg - 1][j] == Double.NEGATIVE_INFINITY) {
                        continue;  // skip impossible states
                    }
                    // Score of segment 'segIndex' covering keys (j+1) to i (inclusive):
                    // count of segIndex in that range = count[segIndex][i] - count[segIndex][j]
                    int segScore = count[segIndex][i] - count[segIndex][j];
                    double totalScore = dp[seg - 1][j] + segScore;
                    if (totalScore > bestScore) {
                        bestScore = totalScore;
                        bestCut = j;
                    }
                }
                dp[seg][i] = bestScore;
                choiceIndex[seg][i] = bestCut;
            }
        }

        // The maximum total score is dp[K][n]. Now reconstruct one optimal partition.
        List<Long> boundaries = new ArrayList<>();
        int segments = K;
        int endIndex = n;
        while (segments > 0) {
            int cutIndex = choiceIndex[segments][endIndex];
            // If cutIndex is -1 and segments > 0, it means no valid partition (should not happen if input is consistent)
            if (cutIndex < 0) break;
            // The boundary is the key at position 'cutIndex' (this is the last key of the previous segment)
            Long boundaryKey = keys.get(cutIndex);
            boundaries.add(boundaryKey);
            endIndex = cutIndex;
            segments -= 1;
        }
        // The loop above collects boundaries in reverse order (from last cut to first cut), so reverse the list:
        Collections.reverse(boundaries);

        // If only one optimal partition is needed, we would return 'boundaries' at this point.
        // However, to apply the balance criterion, let's check for ties and adjust if necessary.

        // Check if multiple partitions might have the same max score:
        double maxScore = dp[K][n];
        List<List<Long>> optimalBoundarySets = new ArrayList<>();
        // Use backtracking to collect all optimal boundary sets (if needed for tie-breaking)
        backtrackOptimal(optimalBoundarySets, new ArrayList<>(), choiceIndex, dp, count, K, n, keys, maxScore);
        if (!optimalBoundarySets.isEmpty()) {
            // Include the one we already have (boundary set from choiceIndex) if not included
            optimalBoundarySets.add(boundaries);
        } else {
            optimalBoundarySets.add(boundaries);
        }

        // Select the most balanced partition among the optimal ones
        List<Long> bestBalanced = null;
        double bestRatio = Double.MAX_VALUE;
        for (List<Long> part : optimalBoundarySets) {
            // Calculate segment lengths (number of keys in each segment for this partition)
            List<Integer> lengths = new ArrayList<>();
            long prevKey = keys.get(0);
            int startIdx = 0;
            // Derive indices from boundary keys
            List<Integer> boundaryIndices = new ArrayList<>();
            for (Long b : part) {
                // find index of boundary key in sorted list
                int bi = Collections.binarySearch(keys, b);
                if (bi < 0) continue;
                boundaryIndices.add(bi);
            }
            // Add last index as boundary for convenience
            boundaryIndices.add(n - 1);
            // Now compute lengths: difference between consecutive boundary indices
            int prevIndex = -1;
            for (int idx : boundaryIndices) {
                int len;
                if (prevIndex == -1) {
                    // first segment from 0 to idx
                    len = idx + 1;
                } else {
                    // segment from prevIndex+1 to idx
                    len = idx - (prevIndex + 1) + 1;
                }
                lengths.add(len);
                prevIndex = idx;
            }
            // (The number of segments here should equal K.)
            // Determine longest and shortest
            int maxLen = Collections.max(lengths);
            int minLen = Collections.min(lengths);
            double ratio = (double) maxLen / minLen;
            if (ratio <= 5.0 && ratio < bestRatio) {
                bestRatio = ratio;
                bestBalanced = part;
            }
        }
        // If we found a balanced partition among optimal ones, return it; otherwise return the initially found one.
        return Objects.requireNonNullElse(bestBalanced, boundaries);
    }

    // Helper method to backtrack and collect all optimal partitions (boundary sets) achieving maxScore.
    private static void backtrackOptimal(List<List<Long>> results, List<Long> current, int[][] choiceIndex,
                                         double[][] dp, int[][] count, int seg, int idx, List<Long> keys, double maxScore) {
        if (seg == 0 && idx == 0) {
            // reached a valid partition configuration (all segments placed)
            List<Long> solution = new ArrayList<>(current);
            Collections.reverse(solution);  // reverse to get boundaries from smallest to largest key
            results.add(solution);
            return;
        }
        if (seg <= 0) {
            return;
        }
        int segIndex = seg - 1;
        // Try all possible cut positions that yield the optimal score for dp[seg][idx]
        for (int j = seg - 1; j < idx; j++) {
            if (dp[seg - 1][j] == Double.NEGATIVE_INFINITY) continue;
            // Score of segment segIndex covering keys (j+1) to idx
            int segScore = count[segIndex][idx] - count[segIndex][j];
            double total = dp[seg - 1][j] + segScore;
            if (Math.abs(total - dp[seg][idx]) < 1e-9  && Math.abs(total - maxScore) < 1e-9 * seg) {
                // The math.abs checks ensure we match floating values (used double in dp) accounting for precision
                // This condition means: using a cut at j yields the same dp value as stored, hence it's optimal.
                // Also ensure that the total score accumulated so far can still lead to maxScore at the end.
                // The second part (total - maxScore check) is a safety to ensure we only explore optimal paths.
                // Proceed to backtrack for previous segments
                Long boundaryKey = (j > 0 ? keys.get(j - 1) : null);
                if (boundaryKey != null) {
                    current.add(boundaryKey);
                }
                backtrackOptimal(results, current, choiceIndex, dp, count, seg - 1, j, keys, maxScore);
                if (boundaryKey != null) {
                    current.remove(current.size() - 1);
                }
            }
        }
    }
}
