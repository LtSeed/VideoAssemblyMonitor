package nusri.fyp.demo.roboflow.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import nusri.fyp.demo.roboflow.RoboflowConfig;
import nusri.fyp.demo.roboflow.data.AnyData;
import nusri.fyp.demo.roboflow.data.RoboflowRequestData;
import nusri.fyp.demo.roboflow.data.RoboflowResponseData;
import okhttp3.*;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of {@link RequestSender} interface using {@link OkHttpClient} for asynchronous HTTP requests.
 * <br> This class is responsible for sending POST and GET requests to the Roboflow API using the OkHttp client.
 * It processes responses and provides them asynchronously using {@link CompletableFuture}.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@Component
public class RequestSenderOfOKHttp implements RequestSender {

    private static final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper mapper;

    /**
     * Constructor that initializes the {@link ObjectMapper} for serializing and deserializing request and response bodies.
     *
     * @param roboflowConfig The configuration object for Roboflow that provides the ObjectMapper.
     */
    public RequestSenderOfOKHttp(RoboflowConfig roboflowConfig) {
        mapper = roboflowConfig.getObjectMapper();
    }

    /**
     * Sends an asynchronous POST request to the specified URI.
     * <br> If request data is provided, it is serialized and sent as the request body.
     * If no data is provided, a POST request with no body is sent.
     *
     * @param BASE_URI The URI to which the POST request will be sent.
     * @param log The logger to log request details.
     * @param data The request data to be sent (can be null).
     * @param success The expected success response type.
     * @param failure The expected failure response type (can be null).
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
        // Validate the data type
        if (data != null && requestClass != null && data.getClass() != requestClass) {
            throw new IllegalArgumentException("data must be instance of " + requestClass.getName());
        }

        // Prepare the future to be returned asynchronously
        CompletableFuture<RoboflowResponseData> future = new CompletableFuture<>();

        try {
            Request request;
            if (data != null) {
                // Convert data to JSON or other required format
                RequestBody body = data.toRequestBody(mapper);
                request = new Request.Builder()
                        .url(BASE_URI.toURL())
                        .post(body)
                        .build();
            } else {
                request = new Request.Builder()
                        .url(BASE_URI.toURL())
                        .post(RequestBody.create(new byte[0], Objects.requireNonNull(MediaType.parse("application/json"))))
                        .build();
            }

            // Send request asynchronously
            getFuture(log, success, failure, future, request);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            future.completeExceptionally(e);
        }

        return future;
    }

    /**
     * Sends an asynchronous GET request to the specified URI.
     * <br> The request is sent with an "Accept" header set to "application/json".
     *
     * @param BASE_URI The URI to which the GET request will be sent.
     * @param log The logger to log request details.
     * @param success The expected success response type.
     * @param failure The expected failure response type (can be null).
     * @return A {@link CompletableFuture} containing the response data.
     */
    @Override
    public CompletableFuture<RoboflowResponseData> getAsync(URI BASE_URI,
                                                            Logger log,
                                                            Class<? extends RoboflowResponseData> success,
                                                            @Nullable Class<? extends RoboflowResponseData> failure) {
        CompletableFuture<RoboflowResponseData> future = new CompletableFuture<>();

        try {
            Request request = new Request.Builder()
                    .url(BASE_URI.toURL())
                    .get()
                    .build();

            // Send the request asynchronously
            getFuture(log, success, failure, future, request);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            future.completeExceptionally(e);
        }

        return future;
    }

    /**
     * Helper method to process the HTTP response asynchronously.
     * <br> It processes the response and returns the result by deserializing it into the expected response type (success or failure).
     *
     * @param log The logger to log request details.
     * @param success The expected success response type.
     * @param failure The expected failure response type (can be null).
     * @param future The future that will hold the response data.
     * @param request The HTTP request to be sent.
     */
    private void getFuture(Logger log, Class<? extends RoboflowResponseData> success, @Nullable Class<? extends RoboflowResponseData> failure, CompletableFuture<RoboflowResponseData> future, Request request) {
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@Nonnull Call call, @Nonnull IOException e) {
                log.error("Request failed: {}", e.getMessage(), e);
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(@Nonnull Call call, @Nonnull Response response) {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful() || failure == null) {
                        try {
                            if (success != AnyData.class) {
                                RoboflowResponseData obj = null;
                                if (responseBody != null) {
                                    obj = mapper.readValue(responseBody.string(), success);
                                }
                                future.complete(obj);
                            } else {
                                if (responseBody != null) {
                                    future.complete(new AnyData(responseBody.string()));
                                }
                            }
                        } catch (IOException e) {
                            log.error("JSON parse error: {}", e.getMessage(), e);
                            future.complete(new AnyData(e.getMessage()));
                        }
                    } else {
                        String errorBody = responseBody != null ? responseBody.string() : null;
                        log.error(errorBody);
                        try {
                            RoboflowResponseData obj = mapper.readValue(errorBody, failure);
                            future.complete(obj);
                        } catch (IOException e) {
                            log.error("JSON parse error: {}", e.getMessage(), e);
                            future.complete(new AnyData(e.getMessage()));
                        }
                    }
                } catch (Exception e) {
                    log.error("Unknown error: {}", e.getMessage(), e);
                    future.completeExceptionally(e);
                }
            }
        });
    }
}
