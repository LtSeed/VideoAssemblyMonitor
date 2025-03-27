package nusri.fyp.demo.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Quota configuration class, used to describe the quota mode and step configurations for a {@link Preset} or a workflow.
 * <br> For example, quotaMode can be "avgOffset", "confidence", or "disabled", paired with multiple {@link SingleQuota} entries.
 */
@Data
public class QuotaConfig {
    private static final Logger log = LoggerFactory.getLogger(QuotaConfig.class);

    /**
     * The quota mode, such as "avgOffset", "confidence", or "disabled".
     */
    private String quotaMode;

    /**
     * A list of quota entries, each implementing the {@link SingleQuota} interface, which may represent offset or confidence modes, etc.
     */
    private List<SingleQuota> quotas;

    /**
     * No-argument constructor.
     */
    public QuotaConfig() {
    }

    /**
     * Initializes the quota configuration based on the given {@link Preset}'s node information, defaulting to "avgOffset" mode.
     * <br> Each node's {@code realQuota} will automatically generate upper and lower boundaries and ratios.
     *
     * @param preset The preset from which the quota configuration is constructed.
     */
    public QuotaConfig(Preset preset) {
        quotaMode = "avgOffset";
        Stream<SingleQuotaOfOffset> quota = preset.getNodes().stream().map(o -> {
            SingleQuotaOfOffset singleQuotaOfOffset = new SingleQuotaOfOffset();
            singleQuotaOfOffset.setProc(o.getName());
            singleQuotaOfOffset.setQuota(o.getRealQuota());
            singleQuotaOfOffset.setUpRatio("0.4");
            singleQuotaOfOffset.setDownRatio("0.4");
            singleQuotaOfOffset.setUpBoundary(String.valueOf(1.4 * o.getRealQuota()));
            singleQuotaOfOffset.setDownBoundary(String.valueOf(0.6 * o.getRealQuota()));
            return singleQuotaOfOffset;
        });
        this.quotas = quota.collect(Collectors.toList());
    }

    /**
     * Parses and generates a quota configuration from a JSON string using the given {@link ObjectMapper}.
     *
     * @param json   A JSON string containing quota configuration information.
     * @param mapper Jackson's object mapper for deserialization.
     * @throws RuntimeException If parsing fails.
     */
    public QuotaConfig(String json, ObjectMapper mapper) {
        try {
            QuotaConfig tmp = mapper.readValue(json, QuotaConfig.class);
            this.quotaMode = tmp.getQuotaMode();
            this.quotas = tmp.getQuotas().stream().peek(o -> {
                double v = Double.parseDouble(o.getQuota());
                o.setQuota(v > 1000.0 ? v / 1000 : v);
            }).toList();
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse QuotaConfig from JSON.", e);
        }
    }

    /**
     * Serializes the current object into a JSON string.
     *
     * @param mapper The Jackson object mapper for serialization.
     * @return The JSON string.
     * @throws JsonProcessingException If serialization fails.
     */
    public String serialize(ObjectMapper mapper) throws JsonProcessingException {
        return mapper.writeValueAsString(this);
    }
}