package nusri.fyp.demo.roboflow.request;

import jakarta.annotation.Nullable;
import nusri.fyp.demo.roboflow.data.RoboflowRequestData;
import nusri.fyp.demo.roboflow.data.RoboflowResponseData;
import org.slf4j.Logger;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for sending HTTP requests to the Roboflow API.
 * <br> This interface defines methods for sending asynchronous HTTP requests, both POST and GET, to the Roboflow API with appropriate request and response data handling.
 *
 * @author Liu Binghong
 * @since 1.0
 */
public interface RequestSender {

    /**
     * Asynchronously sends a POST request to the specified URI with optional request data.
     * <br> This method constructs an HTTP POST request and returns a {@link CompletableFuture} that contains the response data.
     *
     * @param BASE_URI The base URI for the request (Roboflow API endpoint).
     * @param log The logger instance to record logs during the request process.
     * @param data The request data to be sent in the POST request, can be {@code null} if no data is required.
     * @param success The class representing the expected success response type.
     * @param failure The class representing the expected failure response type, can be {@code null} if not used.
     * @param requestClass The class representing the request data type, can be {@code null} if not used.
     * @return A {@link CompletableFuture} that will contain the {@link RoboflowResponseData} when the request is completed.
     */
    CompletableFuture<RoboflowResponseData> postAsync(
            URI BASE_URI,
            Logger log,
            @Nullable RoboflowRequestData data,
            Class<? extends RoboflowResponseData> success,
            @Nullable Class<? extends RoboflowResponseData> failure,
            @Nullable Class<? extends RoboflowRequestData> requestClass
    );

    /**
     * Asynchronously sends a GET request to the specified URI.
     * <br> This method constructs an HTTP GET request and returns a {@link CompletableFuture} that contains the response data.
     *
     * @param BASE_URI The base URI for the request (Roboflow API endpoint).
     * @param log The logger instance to record logs during the request process.
     * @param success The class representing the expected success response type.
     * @param failure The class representing the expected failure response type, can be {@code null} if not used.
     * @return A {@link CompletableFuture} that will contain the {@link RoboflowResponseData} when the request is completed.
     */
    CompletableFuture<RoboflowResponseData> getAsync(
            URI BASE_URI,
            Logger log,
            Class<? extends RoboflowResponseData> success,
            @Nullable Class<? extends RoboflowResponseData> failure
    );
}
