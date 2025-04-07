package nusri.fyp.demo.roboflow.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import nusri.fyp.demo.roboflow.RoboflowConfig;
import nusri.fyp.demo.roboflow.data.*;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of the {@link RequestSender} interface using the {@link HttpClient} for sending HTTP requests asynchronously.
 * <br> This class handles both POST and GET requests to the Roboflow API and processes the responses using {@link CompletableFuture}.
 * <br> It is deprecated in favor of a newer, more efficient HTTP client implementation.
 * @deprecated Can not pass test.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@Component
@Deprecated
public class RequestSenderOfHttpClient implements RequestSender {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper;

    /**
     * Asynchronously sends a POST request to the Roboflow API.
     * <br> If data is provided, it is serialized and sent as the body of the request.
     * <br> If no data is provided, the request is sent with no body.
     *
     * @param BASE_URI The URI to which the POST request will be sent.
     * @param log The logger used to log request information.
     * @param data The request data to be sent (can be null).
     * @param success The class type of the expected success response.
     * @param failure The class type of the expected failure response (can be null).
     * @param requestClass The class type of the request data (can be null).
     * @return A {@link CompletableFuture} containing the response data.
     */
    @Override
    public CompletableFuture<RoboflowResponseData> postAsync(URI BASE_URI,
                                                             Logger log,
                                                             @Nullable RoboflowRequestData data,
                                                             Class<? extends RoboflowResponseData> success,
                                                             @Nullable Class<? extends RoboflowResponseData> failure,
                                                             @Nullable Class<? extends RoboflowRequestData> requestClass) {
        if (data != null && requestClass != null && data.getClass() != requestClass) {
            throw new IllegalArgumentException("data must be instance of " + requestClass.getName());
        }

        try {
            HttpRequest request;
            if (data != null) {
                log.info("posting data: {} to URI: {}", data, BASE_URI.toURL());
                HttpRequest.BodyPublisher bodyPublisher = data.toBodyPublisher(mapper);
                request = HttpRequest.newBuilder()
                        .uri(BASE_URI)
                        .POST(bodyPublisher)
                        .header("Content-Type", "application/json")
                        .build();
                log.info("sending request Content Length: {}", request.bodyPublisher().orElseThrow().contentLength());
            }
            else {
                log.info("posting NODATA");
                request = HttpRequest.newBuilder()
                        .uri(BASE_URI)
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .header("Accept", "application/json")
                        .build();
            }

            return getCompletableFuture(httpClient, log, success, failure, request);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Asynchronously sends a GET request to the Roboflow API.
     * <br> The request is sent with the "Accept" header specifying JSON responses.
     *
     * @param BASE_URI The URI to which the GET request will be sent.
     * @param log The logger used to log request information.
     * @param success The class type of the expected success response.
     * @param failure The class type of the expected failure response (can be null).
     * @return A {@link CompletableFuture} containing the response data.
     */
    @Override
    public CompletableFuture<RoboflowResponseData> getAsync(URI BASE_URI,
                                                            Logger log,
                                                            Class<? extends RoboflowResponseData> success,
                                                            @Nullable Class<? extends RoboflowResponseData> failure) {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(BASE_URI)
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            return getCompletableFuture(httpClient, log, success, failure, request);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Helper method to process the HTTP response asynchronously.
     * <br> The method processes the response and returns the result, deserializing it into the expected success or failure type.
     *
     * @param httpClient The HTTP client used to send the request.
     * @param log The logger used to log request information.
     * @param success The class type of the expected success response.
     * @param failure The class type of the expected failure response (can be null).
     * @param request The HTTP request to be sent.
     * @return A {@link CompletableFuture} containing the response data.
     */
    public CompletableFuture<RoboflowResponseData> getCompletableFuture(HttpClient httpClient,
                                                                        Logger log,
                                                                        Class<? extends RoboflowResponseData> success,
                                                                        @Nullable Class<? extends RoboflowResponseData> failure,
                                                                        HttpRequest request) {
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(
                response -> {
                    if (response.statusCode() == 200 || failure == null) {
                        try {
                            if (success != AnyData.class) {
                                return mapper.readValue(response.body(), success);
                            }
                            else return new AnyData(response.body());
                        } catch (JsonProcessingException e) {
                            log.error(e.getMessage());
                            return new AnyData(e.getMessage());
                        }
                    } else {
                        log.error(response.body());
                        try {
                            return mapper.readValue(response.body(), failure);
                        } catch (JsonProcessingException e) {
                            log.error(e.getMessage());
                            return new AnyData(e.getMessage());
                        }
                    }
                }
        );
    }

    /**
     * Constructor that initializes the {@link ObjectMapper} for serializing and deserializing request and response bodies.
     *
     * @param roboflowConfig The Roboflow configuration object containing the object mapper.
     */
    RequestSenderOfHttpClient(RoboflowConfig roboflowConfig) {
        mapper = roboflowConfig.getObjectMapper();
    }
}
