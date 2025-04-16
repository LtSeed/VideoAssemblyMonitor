package nusri.fyp.demo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nusri.fyp.demo.annotation.Config;
import nusri.fyp.demo.entity.ConfigChangeLog;
import nusri.fyp.demo.entity.PythonServer;
import nusri.fyp.demo.entity.QuotaConfig;
import nusri.fyp.demo.entity.RoboflowWorkflow;
import nusri.fyp.demo.repository.ConfigChangeLogRepository;
import nusri.fyp.demo.repository.PresetRepository;
import nusri.fyp.demo.repository.PythonServerRepository;
import nusri.fyp.demo.repository.RoboflowWorkflowRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

/**
 * <b>Service class responsible for managing configuration settings in the application.</b>
 * <br><br>
 * This class performs the following functions:
 * <ul>
 *   <li><b>Startup Loading:</b> Loads the most recent configuration from the database at application startup
 *       (see {@link #afterPropertiesSet()}). If none is found, applies default values
 *       specified in {@link Config#defaultValue()}.</li>
 *   <li><b>Persistence:</b> When configuration changes are made, they are serialized and stored in the database
 *       via {@link #updateConfig()}.</li>
 *   <li><b>Default Values:</b> Fields annotated with {@link Config} will use the specified default value if no
 *       saved configuration is available (see {@link #useDefaults()}).</li>
 *   <li><b>Quota Configurations:</b> Manages quota settings for different presets through
 *       {@link #getQuotaConfig(String)} and {@link #addQuotaConfig(String, QuotaConfig)}.</li>
 *   <li><b>Model Mappings:</b> Tracks a mapping of preset names to model descriptors, e.g.
 *       {@code { "default": "roboflow@tomcai@detect-count-and-visualize-2" }},
 *       accessed with {@link #getUseModel(String)}.</li>
 * </ul>
 * <br>
 * Configuration data is persisted in the database as JSON via {@link ObjectMapper}. Each field annotated
 * with {@link Config} will be included in the serialized configuration map (see {@link #serializeConfig()}
 * and {@link #loadConfig(Object, String)}).
 * <br><br>
 * <b>Usage:</b>
 * <br>
 * Typically, other services or controllers call methods like {@link #getUseModel(String)} to retrieve model
 * configurations for a given preset, or {@link #getQuotaConfig(String)} to get timeouts/limits, etc.
 * Administrators may update these configurations at runtime, triggering a new database entry.
 *
 * @author Liu Binghong
 * @since 1.0
 * @see ConfigChangeLogRepository
 * @see ConfigChangeLog
 * @see PresetRepository
 * @see PythonServerRepository
 * @see RoboflowWorkflowRepository
 * @see QuotaConfig
 * @see Config
 */
@SuppressWarnings("JavadocLinkAsPlainText")
@Service
@Getter
@Setter
@Slf4j
public class ConfigService implements InitializingBean {

    /**
     * Repository for creating and retrieving logs of configuration changes.
     */
    private final ConfigChangeLogRepository configChangeLogRepository;

    /**
     * JSON mapper for serialization and deserialization of configuration data.
     */
    private final ObjectMapper objectMapper;

    /**
     * Repository for managing system presets.
     */
    private final PresetRepository presetRepository;

    /**
     * Repository for managing Python server information.
     */
    private final PythonServerRepository pythonServerRepository;

    /**
     * Repository for managing Roboflow workflows.
     */
    private final RoboflowWorkflowRepository roboflowWorkflowRepository;

    /**
     * The Python server host used for image processing endpoints.
     * <br> Defaults to {@code "http://localhost"}.
     */
    @Config(defaultValue = "\"http://localhost\"")
    private String pythonServerHost;

    /**
     * The main port for the Python server that provides the /instances endpoint.
     * <br> Defaults to {@code "5000"}.
     */
    @Config(defaultValue = "\"5000\"")
    private String pythonServerMainPort;

    /**
     * The Roboflow host address (e.g., "http://localhost" or a remote server).
     */
    @Config(defaultValue = "\"http://localhost\"")
    private String roboflowHost;

    /**
     * The port for communicating with Roboflow services.
     */
    @Config(defaultValue = "\"9001\"")
    private String roboflowPort;

    /**
     * The file system path where video files are stored.
     * <br> Defaults to {@code "D:\\save"}.
     */
    @Config(defaultValue = "\"D:\\\\save\"")
    private String videoPath;

    /**
     * A map of preset names to model descriptors.
     * <br> e.g. {@code {"default": "roboflow@tomcai@detect-count-and-visualize-2"}}
     */
    @Config(defaultValue = "{\"default\":\"roboflow@tomcai@detect-count-and-visualize-2\"}")
    private Map<String, String> useModel;

    /**
     * The number of frames to skip when processing videos, stored as a string.
     * <br> Defaults to {@code "10"}.
     */
    @Config(defaultValue = "\"10\"")
    private String frameInterval;

    /**
     * The API key used when interacting with Roboflow.
     */
    @Config(defaultValue = "\"Y8Sj6ELLMEKYaFs7Vypv\"")
    private String roboflowApiKey;

    /**
     * A map of custom quota configurations (timeouts, limits, etc.) indexed by preset name.
     */
    @Config(defaultValue = "{}")
    private Map<String, String> modelQuotaConfig;

    /**
     * Constructs a {@link ConfigService} with all necessary repositories and the JSON object mapper.
     *
     * @param configChangeLogRepository the repository for storing configuration change logs
     * @param objectMapper the JSON mapper for serializing config fields
     * @param presetRepository the repository for managing presets
     * @param pythonServerRepository the repository for Python server info
     * @param roboflowWorkflowRepository the repository for Roboflow workflow info
     */
    public ConfigService(ConfigChangeLogRepository configChangeLogRepository,
                         ObjectMapper objectMapper,
                         PresetRepository presetRepository,
                         PythonServerRepository pythonServerRepository,
                         RoboflowWorkflowRepository roboflowWorkflowRepository) {
        this.configChangeLogRepository = configChangeLogRepository;
        this.objectMapper = objectMapper;
        this.presetRepository = presetRepository;
        this.pythonServerRepository = pythonServerRepository;
        this.roboflowWorkflowRepository = roboflowWorkflowRepository;
    }

    /**
     * Initializes the service by loading the latest configuration from the database.
     * <br>If no valid saved configuration is found, applies defaults via {@link #useDefaults()}.
     *
     * @throws Exception if loading or parsing configuration fails
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        ConfigChangeLog latestLog = configChangeLogRepository.findTopByOrderByTimestampDesc();
        if (latestLog != null && latestLog.getConfig() != null && !latestLog.getConfig().trim().equals("{}")) {
            try {
                log.info("Latest config: {}", latestLog.getConfig());
                this.loadConfig(this, latestLog.getConfig());
                log.info("quota config: {}", this.modelQuotaConfig);
            } catch (Exception e) {
                this.useDefaults();
            }
        } else {
            log.error("UseDefaults ");
            this.useDefaults();
        }
    }

    /**
     * Persists the current configuration by serializing annotated fields to JSON and
     * saving them in a {@link ConfigChangeLog} record.
     *
     * @throws Exception if serialization or database write operations fail
     */
    public void updateConfig() throws Exception {
        String serializedConfig = this.serializeConfig();
        ConfigChangeLog log = new ConfigChangeLog();
        log.setConfig(serializedConfig);
        log.setTimestamp(System.currentTimeMillis());
        configChangeLogRepository.save(log);
    }

    /**
     * Serializes all fields annotated with {@link Config} into a JSON string.
     * <br> Reflection is used to identify annotated fields.
     *
     * @return a JSON string representing all current configuration values
     * @throws Exception if an error occurs during serialization
     */
    public String serializeConfig() throws Exception {
        Map<String, Object> configMap = new HashMap<>();
        for (Field field : ConfigService.class.getDeclaredFields()) {
            if (field.isAnnotationPresent(Config.class)) {
                field.setAccessible(true);
                configMap.put(field.getName(), field.get(this));
            }
        }
        return objectMapper.writeValueAsString(configMap);
    }

    /**
     * Loads configuration values from a JSON string into the given object's annotated fields.
     * <br> This allows partial updates where only some fields are overwritten if present in the JSON.
     *
     * @param bean the object to apply the configuration to (usually {@code this})
     * @param json the JSON string containing configuration key-value pairs
     * @throws Exception if there is an error parsing or setting fields
     */
    public void loadConfig(Object bean, String json) throws Exception {
        Map<String, Object> configMap = objectMapper.readValue(json, new TypeReference<>() {});
        for (Field field : bean.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Config.class)) {
                field.setAccessible(true);
                if (configMap.containsKey(field.getName())) {
                    Object value = configMap.get(field.getName());
                    field.set(bean, objectMapper.convertValue(value, field.getType()));
                }
            }
        }
    }

    /**
     * Applies default values to any annotated fields that are currently {@code null}.
     * <br> The defaults come from {@link Config#defaultValue()}.
     *
     * @throws Exception if an error occurs reading or setting default values
     */
    public void useDefaults() throws Exception {
        Class<?> clazz = this.getClass();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Config.class)) {
                    field.setAccessible(true);
                    if (field.get(this) == null) {
                        Config config = field.getAnnotation(Config.class);
                        String defaultValue = config.defaultValue();
                        if (!defaultValue.isEmpty()) {
                            field.set(this, objectMapper.readValue(defaultValue, field.getType()));
                        }
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    /**
     * Retrieves the model identifier (e.g., "roboflow@tomcai@detect-count-and-visualize-2") for the specified preset.
     * <br>If no explicit mapping is found, returns the default model descriptor.
     *
     * @param presetName the name of the preset
     * @return the model descriptor for the preset
     */
    public String getUseModel(String presetName) {
        return useModel.getOrDefault(presetName, useModel.get("default"));
    }

    /**
     * Retrieves a list of all available model descriptors (for both Python servers and Roboflow workflows).
     * <br> This method is cacheable to improve performance.
     *
     * @return a list of {@link String} describing each model (e.g., "python@http://localhost@5000", "roboflow@tomcai@detect-count-and-visualize-2")
     */
    @Cacheable(value = "getAllLegalModel")
    public List<String> getAllLegalModel() {
        List<PythonServer> pythonServers = pythonServerRepository.findAll();
        List<RoboflowWorkflow> roboflowWorkflows = roboflowWorkflowRepository.findAll();

        List<String> models = new ArrayList<>();
        pythonServers.forEach(pythonServer -> models.add("python@" + pythonServer.toString()));
        roboflowWorkflows.forEach(workflow -> models.add("roboflow@" + workflow.toString()));

        return models;
    }

    /**
     * Retrieves a {@link QuotaConfig} for the specified preset, either from {@link #modelQuotaConfig} (cached)
     * or by constructing it from the preset information in the database.
     * <br> Results are cached in "presetConfigCache" to avoid repetitive lookups.
     *
     * @param presetName the name of the preset
     * @return the corresponding {@link QuotaConfig}
     */
    @Cacheable(value = "presetConfigCache", key = "#presetName")
    public QuotaConfig getQuotaConfig(String presetName) {
        log.debug("preset: {}", presetName);
        if (modelQuotaConfig.containsKey(presetName)) {
            return new QuotaConfig(modelQuotaConfig.get(presetName), objectMapper);
        }
        return new QuotaConfig(Objects.requireNonNull(
                presetRepository.findPresetByName(presetName).stream().findAny().orElse(null)
        ));
    }

    /**
     * Adds or updates the {@link QuotaConfig} for a given preset name and immediately saves the
     * updated configuration to the database via {@link #updateConfig()}.
     *
     * @param presetName  the name of the preset to modify
     * @param quotaConfig the new quota configuration
     * @throws Exception if serialization or database write fails
     */
    public void addQuotaConfig(String presetName, QuotaConfig quotaConfig) throws Exception {
        this.modelQuotaConfig.put(presetName, quotaConfig.serialize(objectMapper));
        updateConfig();
    }

    /**
     * Retrieve all quota configs that has been changed by the frontend.
     *
     * @return All quota configs.
     */
    public Map<String, QuotaConfig> getAllQuotaConfig() {
        Map<String, QuotaConfig> quotaConfigs = new HashMap<>();
        modelQuotaConfig.forEach((presetName, quotaConfig) -> quotaConfigs.put(presetName, new QuotaConfig(quotaConfig, objectMapper)));
        if (quotaConfigs.containsKey("default")) {
            return quotaConfigs;
        }
        QuotaConfig value = new QuotaConfig();
        value.setQuotaMode("disabled");
        quotaConfigs.put("default", value);
        return quotaConfigs;
    }
}
