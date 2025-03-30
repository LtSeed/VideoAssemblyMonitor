package nusri.fyp.demo.roboflow.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import nusri.fyp.demo.roboflow.RoboflowConfig;
import nusri.fyp.demo.roboflow.request.RequestSenderOfHttpClient;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Abstract class representing request data for Roboflow API operations.
 * <br> This class provides methods for serializing the request data to different formats such as HTTP request bodies and JSON payloads.
 * <br> Subclasses of this class represent specific request data types for different Roboflow API requests.
 */
@Slf4j
public abstract class RoboflowRequestData {

    /**
     * Converts the current request data object to a {@link HttpRequest.BodyPublisher} with a JSON payload.
     * <br> The JSON payload is serialized using the provided {@link ObjectMapper}.
     * <br> The JSON content is also saved to a file named "jsonPayload.json" for debugging purposes.
     *
     * @param mapper The {@link ObjectMapper} used to serialize the request data.
     * @return A {@link HttpRequest.BodyPublisher} containing the serialized JSON data.
     */
    public HttpRequest.BodyPublisher toBodyPublisher(ObjectMapper mapper) {
        try {
            String jsonPayload = mapper.writeValueAsString(this);
            return HttpRequest.BodyPublishers.ofString(jsonPayload);
        } catch (IOException e) {
            log.error(e.getMessage());
            return HttpRequest.BodyPublishers.noBody(); // Return an empty body in case of failure
        }
    }

    /**
     * Converts the current request data object to a {@link RequestBody} for use with {@link RequestSenderOfHttpClient}.
     * <br> The request body is serialized to JSON format using the provided {@link ObjectMapper}.
     *
     * @param mapper The {@link ObjectMapper} used to serialize the request data.
     * @return A {@link RequestBody} containing the serialized JSON data.
     */
    @SuppressWarnings("deprecation")
    public RequestBody toRequestBody(ObjectMapper mapper) {
        try {
            return RequestBody.create(
                    Objects.requireNonNull(MediaType.parse("application/json")),
                    mapper.writeValueAsString(this)
            );
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return RequestBody.create(Objects.requireNonNull(MediaType.parse("application/json")), "{}");
        }
    }
}
