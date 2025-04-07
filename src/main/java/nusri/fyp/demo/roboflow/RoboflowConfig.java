package nusri.fyp.demo.roboflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.Getter;
import nusri.fyp.demo.roboflow.data.AnyData;
import nusri.fyp.demo.roboflow.data.AnyDataDeserializer;
import nusri.fyp.demo.roboflow.data.AnyDataSerializer;
import nusri.fyp.demo.service.ConfigService;
import org.springframework.stereotype.Component;

/**
 * Configuration class for setting up Roboflow integration.
 * <br> This class configures the Jackson {@link ObjectMapper} with custom serializers and deserializers for {@link AnyData}.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@Component
public class RoboflowConfig {

    @Getter
    private final ObjectMapper objectMapper;
    private final ConfigService configService;

    /**
     * Constructor that initializes the {@link ObjectMapper} with custom serializers and deserializers
     * for handling {@link AnyData}.
     *
     * @param objectMapper The Jackson {@link ObjectMapper} to be configured.
     * @param configService The configuration service for retrieving the Roboflow settings.
     */
    RoboflowConfig(ObjectMapper objectMapper, ConfigService configService) {
        this.objectMapper = objectMapper;
        SimpleModule module = new SimpleModule();
        module.addDeserializer(AnyData.class, new AnyDataDeserializer());
        module.addSerializer(AnyData.class, new AnyDataSerializer());
        objectMapper.registerModule(module);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        this.configService = configService;
    }

    /**
     * Retrieves the host URL for Roboflow API, including the host and port.
     *
     * @return The host and port for Roboflow API.
     */
    public String getHost() {
        return configService.getRoboflowHost() + ":" + configService.getRoboflowPort();
    }

    /**
     * Retrieves the Roboflow API key, removing any surrounding quotes.
     *
     * @return The Roboflow API key.
     */
    public String getApiKey() {
        return configService.getRoboflowApiKey().replace("\"", "");
    }
}
