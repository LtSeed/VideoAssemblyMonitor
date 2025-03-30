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
 * Service class responsible for managing configuration settings in the application.
 * <br>This class is responsible for loading, updating, and serializing configuration values.
 * <br>The configuration values are defined by fields annotated with {@link Config}, and the
 * values are stored in a database log for persistence.
 * <br>The configuration can be reloaded from the database at application startup or when changes
 * are made. Default values for each configuration field can be specified using the {@link Config} annotation.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@Service
@Getter
@Setter
@Slf4j
public class ConfigService implements InitializingBean {

    private final ConfigChangeLogRepository configChangeLogRepository;
    private final ObjectMapper objectMapper;
    private final PresetRepository presetRepository;
    private final PythonServerRepository pythonServerRepository;
    private final RoboflowWorkflowRepository roboflowWorkflowRepository;

    @Config(defaultValue = "\"http://localhost\"")
    private String pythonServerHost;

    @Config(defaultValue = "\"5000\"")
    private String pythonServerMainPort;

    @Config(defaultValue = "\"http://localhost\"")
    private String roboflowHost;

    @Config(defaultValue = "\"9001\"")
    private String roboflowPort;

    @Config(defaultValue = "\"D:\\\\save\"")
    private String videoPath;

    @Config(defaultValue = "{\"default\":\"roboflow@tomcai@detect-count-and-visualize-2\"}")
    private Map<String, String> useModel;

    @Config(defaultValue = "\"10\"")
    private String frameInterval;

    @Config(defaultValue = "\"Y8Sj6ELLMEKYaFs7Vypv\"")
    private String roboflowApiKey;

    @Config(defaultValue = "{}")
    private Map<String, String> modelQuotaConfig;

    /**
     * Constructs a {@link ConfigService} with the given {@link ConfigChangeLogRepository}, {@link ObjectMapper},
     * and {@link PresetRepository}.
     *
     * @param configChangeLogRepository The repository for configuration change logs.
     * @param objectMapper The object mapper used for JSON serialization and deserialization.
     * @param presetRepository The repository for preset data.
     */
    public ConfigService(ConfigChangeLogRepository configChangeLogRepository, ObjectMapper objectMapper, PresetRepository presetRepository, PythonServerRepository pythonServerRepository, RoboflowWorkflowRepository roboflowWorkflowRepository) {
        this.configChangeLogRepository = configChangeLogRepository;
        this.objectMapper = objectMapper;
        this.presetRepository = presetRepository;
        this.pythonServerRepository = pythonServerRepository;
        this.roboflowWorkflowRepository = roboflowWorkflowRepository;
    }


    /**
     * Called after all properties are set. This method attempts to load the latest configuration from the change log.
     * <br>If a valid configuration is found, it will be applied. Otherwise, default values will be used.
     *
     * @throws Exception If there is an error while loading or applying the configuration.
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
     * Updates the configuration in the database by serializing the current configuration fields.
     *
     * @throws Exception If there is an error during serialization or database write.
     */
    public void updateConfig() throws Exception {
        String serializedConfig = this.serializeConfig();
        ConfigChangeLog log = new ConfigChangeLog();
        log.setConfig(serializedConfig);
        log.setTimestamp(System.currentTimeMillis());
        configChangeLogRepository.save(log);
    }

    /**
     * Serializes all configuration fields annotated with {@link Config} into a JSON string.
     *
     * @return A JSON string representing the current configuration.
     * @throws Exception If serialization fails.
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
     * Loads the configuration from a JSON string and applies the values to the corresponding fields.
     *
     * @param bean The target object to load the configuration into.
     * @param json The JSON string representing the configuration.
     * @throws Exception If there is an error during deserialization or reflection.
     */
    public void loadConfig(Object bean, String json) throws Exception {
        Map<String, Object> configMap = objectMapper.readValue(json, new TypeReference<>() {
        });
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
     * Initializes the configuration using the default values specified in the {@link Config} annotations.
     *
     * @throws Exception If there is an error during default value application.
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
     * Retrieves the model name associated with a given preset name from the {@code useModel} configuration.
     * <br>If the preset name is not configured, the default model name is returned.
     *
     * @param presetName The name of the preset to retrieve the model for.
     * @return The model name associated with the preset, such as "yolo", or the default model if not configured.
     */
    public String getUseModel(String presetName) {
        return useModel.getOrDefault(presetName, useModel.get("default"));
    }

    @Cacheable(value = "getAllLegalModel")
    public List<String> getAllLegalModel() {
        List<PythonServer> pythonServers = pythonServerRepository.findAll();
        List<RoboflowWorkflow> roboflowWorkflows = roboflowWorkflowRepository.findAll();

        List<String> models = new ArrayList<>();

        pythonServers.forEach(pythonServer -> models.add("python@" + pythonServer.toString()));
        roboflowWorkflows.forEach(pythonServer -> models.add("roboflow@" + pythonServer.toString()));

        return models;
    }

    /**
     * Retrieves the quota configuration for a given preset name from the local cache or database.
     * <br>If the configuration is cached, the cached value is returned; otherwise, it is retrieved from the database.
     *
     * @param presetName The preset name for which the quota configuration is to be retrieved.
     * @return The parsed quota configuration object, which is an instance of {@link QuotaConfig}.
     */
    @Cacheable(value = "presetConfigCache", key = "#presetName")
    public QuotaConfig getQuotaConfig(String presetName) {
        log.info("preset: {}", presetName);
        if (modelQuotaConfig.containsKey(presetName)) {
            return new QuotaConfig(modelQuotaConfig.get(presetName), objectMapper);
        }
        return new QuotaConfig(Objects.requireNonNull(presetRepository.findPresetByName(presetName).stream().findAny().orElse(null)));
    }

    /**
     * Adds or updates the quota configuration for a specific preset name, and immediately writes the change to the database log.
     * <br>This method ensures that the new quota configuration is serialized and persisted for the given preset.
     *
     * @param presetName The name of the preset for which the quota configuration is to be added or updated.
     * @param quotaConfig The new quota configuration to be applied for the preset.
     * @throws Exception If an error occurs during serialization or database write.
     */
    public void addQuotaConfig(String presetName, QuotaConfig quotaConfig) throws Exception {
        this.modelQuotaConfig.put(presetName, quotaConfig.serialize(objectMapper));
        updateConfig();
    }

}
