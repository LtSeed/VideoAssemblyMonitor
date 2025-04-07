package nusri.fyp.demo.roboflow.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * A class representing a generic response or request data container for Roboflow API operations.
 * <br> This class extends {@link RoboflowRequestData} and implements {@link RoboflowResponseData}.
 * <br> It is used for holding raw data as a string, typically for scenarios where the response is not specifically typed.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnyData extends RoboflowRequestData implements RoboflowResponseData {

    /**
     * The raw data as a string.
     * <br> This field holds the data received from or sent to the Roboflow API.
     */
    String data;

    /**
     * Constructor to initialize the {@link AnyData} object with the provided data.
     *
     * @param data The raw data as a string.
     */
    public AnyData(String data) {
        this.data = data;
    }

    /**
     * Constructor to initialize the {@link AnyData} object with the provided data Map, using the {@link ObjectMapper}.
     *
     * @param stringObjectMap The raw data as a string.
     * @param objectMapper the object mapper.
     * @throws JsonProcessingException throw when objectMapper.writeValueAsString throw it.
     */
    public AnyData(Map<String, Object> stringObjectMap, ObjectMapper objectMapper) throws JsonProcessingException {
        this.data = objectMapper.writeValueAsString(stringObjectMap);
    }
}
