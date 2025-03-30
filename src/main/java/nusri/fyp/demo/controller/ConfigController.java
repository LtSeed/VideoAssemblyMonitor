package nusri.fyp.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nusri.fyp.demo.entity.QuotaConfig;
import nusri.fyp.demo.service.ConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * This controller provides endpoints for managing the configuration of the system. <br>
 * It supports functionality for getting and updating the configuration of various services,<br>
 * including the Python server, Roboflow, and other model-related configurations.<br>
 * It exposes the following operations:
 * <ul>
 *   <li>Retrieve all configurations</li>
 *   <li>Update all configurations</li>
 *   <li>Update Python server host and port</li>
 *   <li>Update Roboflow server host and port</li>
 * </ul>
 * <br>
 * This controller is responsible for interacting with the {@link ConfigService} to retrieve and persist configuration values.<br>
 * <br>
 * @author Liu Binghong
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("/config")
public class ConfigController {

    private final ConfigService configService;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a {@link ConfigController} with the given {@link ConfigService} and {@link ObjectMapper}.
     *
     * @param configService The configuration service to manage configurations.
     * @param objectMapper The object mapper for JSON serialization and deserialization.
     */
    public ConfigController(ConfigService configService, ObjectMapper objectMapper) {
        this.configService = configService;
        this.objectMapper = objectMapper;
    }

    /**
     * Retrieves all configurations in JSON format.
     *
     * @return ResponseEntity containing all configurations serialized as a JSON string.
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllConfigs() {
        try {
            String allConfigJson = configService.serializeConfig();
            return ResponseEntity.ok(allConfigJson);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("获取配置失败: " + e.getMessage());
        }
    }

    /**
     * Updates all configurations from the provided JSON input.
     *
     * @param newConfigJson The new configuration in JSON format.
     * @return ResponseEntity indicating the result of the update operation.
     */
    @PutMapping("/all")
    public ResponseEntity<?> updateAllConfigs(@RequestBody String newConfigJson) {
        try {
            configService.loadConfig(configService, newConfigJson);
            configService.updateConfig();
            return ResponseEntity.ok("全部配置更新成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body("更新配置失败: " + e.getMessage());
        }
    }

    /**
     * Updates the Python server configuration (host and port).
     *
     * @param request The request body containing host and port values.
     * @return ResponseEntity indicating the result of the update operation.
     */
    @PutMapping("/python")
    public ResponseEntity<?> updatePythonConfig(@RequestBody hostAndPort request) {
        try {
            configService.setPythonServerHost(request.getHost());
            configService.setPythonServerMainPort(request.getPort());
            return ResponseEntity.ok("Python 服务器配置更新成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body("更新 Python 配置失败: " + e.getMessage());
        }
    }

    /**
     * Updates the Roboflow server configuration (host and port).
     *
     * @param request The request body containing host and port values.
     * @return ResponseEntity indicating the result of the update operation.
     */
    @PutMapping("/roboflow")
    public ResponseEntity<?> updateRoboflowConfig(@RequestBody hostAndPort request) {
        try {
            configService.setRoboflowHost(request.getHost());
            configService.setRoboflowPort(request.getPort());
            return ResponseEntity.ok("roboflow 服务器配置更新成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("更新 roboflow 配置失败: " + e.getMessage());
        }
    }

    /**
     * A simple DTO class used to encapsulate host and port values for server configurations.
     */
    @Setter
    @Getter
    public static class hostAndPort {
        private String host;
        private String port;

    }

    /**
     * Endpoint for updating the model associated with a given preset name.
     * <br>Example:
     * <pre>
     *  PUT /api/config/model/preset/{presetName}
     *  Body: "yolo"
     * </pre>
     * <br>This will update the model for the given preset name to "yolo".
     *
     * @param presetName The name of the preset whose model is to be updated.
     * @param modelValue The new model value to set for the preset.
     * @return ResponseEntity indicating the success or failure of the operation.
     */
    @PutMapping("/model/preset/{presetName}")
    public ResponseEntity<?> updateUseModelPreset(@PathVariable("presetName") String presetName,
                                                  @RequestBody String modelValue) {
        if (!configService.getAllLegalModel().contains(modelValue)) {
            return ResponseEntity.badRequest().body("No such model setting");
        }
        try {
            java.util.Map<String, String> useModel = configService.getUseModel();
            if (useModel == null) {
                useModel = new java.util.HashMap<>();
            }
            modelValue = modelValue.replace("\"", "");
            useModel.put(presetName, modelValue);

            configService.setUseModel(useModel);
            return ResponseEntity.ok("模型 " + presetName + " 更新为 " + modelValue);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("更新模型失败: " + e.getMessage());
        }
    }

    /**
     * Endpoint for updating the default model across all presets.
     * <br>Example:
     * <pre>
     *  PUT /api/config/model/preset-default
     *  Body: "yolo"
     * </pre>
     * <br>This will set the default model to "yolo" across all presets.
     *
     * @param modelValue The new default model value to set.
     * @return ResponseEntity indicating the success or failure of the operation.
     */
    @PutMapping("/model/preset-default")
    public ResponseEntity<?> updateUseModelPresetAll(@RequestBody String modelValue) {
        try {
            if (!configService.getAllLegalModel().contains(modelValue)) {
                return ResponseEntity.badRequest().body("No such model setting");
            }
            java.util.Map<String, String> useModel = new java.util.HashMap<>();
            useModel.put("default", modelValue);
            configService.setUseModel(useModel);
            return ResponseEntity.ok("模型 default 更新为 " + modelValue);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("更新模型失败: " + e.getMessage());
        }
    }

    /**
     * Endpoint for get the list of all legal model across all presets.
     *
     * @return ResponseEntity indicating all legal model list.
     */
    @GetMapping("/model/all")
    public ResponseEntity<?> getAllLegalModel() {
        return ResponseEntity.ok(configService.getAllLegalModel());
    }


    /**
     * Endpoint for updating the quota for a specific model preset.
     * <br>Example:
     * <pre>
     *  PUT /config/quota/preset/{presetName}
     *  Body: "……" (following {@link QuotaConfig} format)
     * </pre>
     * <br>This will update the quota configuration for the given preset name.
     *
     * @param presetName The preset name whose quota is to be updated.
     * @param quotaConfig The new quota configuration for the preset.
     * @return ResponseEntity indicating the success or failure of the operation.
     */
    @PutMapping("/quota/preset/{presetName}")
    public ResponseEntity<?> updateQuotaConfigPreset(@PathVariable("presetName") String presetName,
                                                     @RequestBody QuotaConfig quotaConfig) {
        try {
            configService.addQuotaConfig(presetName, quotaConfig);
            return ResponseEntity.ok("模型 " + presetName + " quota 更新为 " + quotaConfig);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("更新模型quota失败: " + e.getMessage());
        }
    }

    /**
     * Endpoint for updating the default quota across all model presets.
     * <br>Example:
     * <pre>
     *  PUT /config/quota/preset-default
     *  Body: "……" (following {@link QuotaConfig} format)
     * </pre>
     * <br>This will update the default quota configuration for all model presets.
     *
     * @param quotaConfig The new quota configuration for all presets.
     * @return ResponseEntity indicating the success or failure of the operation.
     */
    @PutMapping("/quota/preset-default")
    public ResponseEntity<?> updateQuotaConfigPresetAll(@RequestBody QuotaConfig quotaConfig) {
        try {
            java.util.Map<String, String> quotaConfigs = new java.util.HashMap<>();
            String serialize = quotaConfig.serialize(objectMapper);
            quotaConfigs.put("default", serialize);
            configService.setModelQuotaConfig(quotaConfigs);
            return ResponseEntity.ok("模型 default 更新为 " + serialize);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("更新模型失败: " + e.getMessage());
        }
    }

}
