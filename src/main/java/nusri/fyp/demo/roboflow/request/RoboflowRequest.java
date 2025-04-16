package nusri.fyp.demo.roboflow.request;

import lombok.extern.slf4j.Slf4j;
import nusri.fyp.demo.roboflow.data.entity.workflow.dto.WorkflowInferenceResponseDTO;
import nusri.fyp.demo.roboflow.RoboflowConfig;
import nusri.fyp.demo.roboflow.data.AnyData;
import nusri.fyp.demo.roboflow.data.RoboflowRequestData;
import nusri.fyp.demo.roboflow.data.RoboflowRequestDataArray;
import nusri.fyp.demo.roboflow.data.RoboflowResponseData;
import nusri.fyp.demo.roboflow.data.response.ExecutionEngineVersions;
import nusri.fyp.demo.roboflow.data.response.WorkflowValidationStatus;
import nusri.fyp.demo.roboflow.data.response.WorkflowsBlocksDescription;
import nusri.fyp.demo.roboflow.data.response.WorkflowsBlocksSchemaDescription;
import nusri.fyp.demo.roboflow.data.request.*;
import nusri.fyp.demo.roboflow.data.response.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Enum representing various Roboflow API requests.
 * Each enum constant corresponds to a specific API endpoint with its associated HTTP method, URI, success and failure response types, and request body type.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@SuppressWarnings("unused")
@Slf4j
public enum RoboflowRequest {
    /**
     * Retrieves device statistics.
     * <br>Endpoint:  `/device/stats`
     * <br>HTTP Method:  `GET`
     * <br>Success Response:  {@link AnyData}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  None
     */
    DEVICE_STATS("GET", "/device/stats", AnyData.class, HTTPValidationError.class, null),

    /**
     * Retrieves server version information.
     * <br>Endpoint:  `/info`
     * <br>HTTP Method:  `GET`
     * <br>Success Response:  {@link ServerVersionInfo}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  None
     */
    INFO("GET", "/info", ServerVersionInfo.class, HTTPValidationError.class, null),

    /**
     * Retrieves model registry descriptions.
     * <br>Endpoint:  `/model/registry`
     * <br>HTTP Method:  `GET`
     * <br>Success Response:  {@link ModelsDescriptions}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  None
     */
    MODEL_REGISTRY("GET", "/model/registry", ModelsDescriptions.class, HTTPValidationError.class, null),

    /**
     * Adds a new model to the registry.
     * <br>Endpoint:  `/model/add`
     * <br>HTTP Method:  `POST`
     * <br>Success Response:  {@link ModelsDescriptions}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  {@link AddModelRequest}
     */
    MODEL_ADD("POST", "/model/add", ModelsDescriptions.class, HTTPValidationError.class, AddModelRequest.class),

    /**
     * Removes a model from the registry.
     * <br>Endpoint:  `/model/remove`
     * <br>HTTP Method:  `POST`
     * <br>Success Response:  {@link ModelsDescriptions}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  {@link ClearModelRequest}
     */
    MODEL_REMOVE("POST", "/model/remove", ModelsDescriptions.class, HTTPValidationError.class, ClearModelRequest.class),

    /**
     * Clears all models from the registry.
     * <br>Endpoint:  `/model/clear`
     * <br>HTTP Method:  `POST`
     * <br>Success Response:  {@link ModelsDescriptions}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  None
     */
    MODEL_CLEAR("POST", "/model/clear", ModelsDescriptions.class, HTTPValidationError.class, null),

    /**
     * Runs object detection inference on an image.
     * <br>Endpoint:  `/infer/object_detection`
     * <br>HTTP Method:  `POST`
     * <br>Success Response:  {@link ObjectDetectionInferenceResponse}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  {@link ObjectDetectionInferenceRequest}
     */
    INFER_OBJECT_DETECTION("POST", "/infer/object_detection", ObjectDetectionInferenceResponse.class, HTTPValidationError.class, ObjectDetectionInferenceRequest.class),

    /**
     * Runs instance segmentation inference on an image.
     * <br>Endpoint:  `/infer/instance_segmentation`
     * <br>HTTP Method:  `POST`
     * <br>Success Response:  {@link InstanceSegmentationInferenceResponse}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  {@link InstanceSegmentationInferenceRequest}
     */
    INFER_INSTANCE_SEGMENTATION("POST", "/infer/instance_segmentation", InstanceSegmentationInferenceResponse.class, HTTPValidationError.class, InstanceSegmentationInferenceRequest.class),

    /**
     * Runs classification inference on an image.
     * <br>Endpoint:  `/infer/classification`
     * <br>HTTP Method:  `POST`
     * <br>Success Response:  {@link ClassificationInferenceResponse}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  {@link ClassificationInferenceRequest}
     */
    INFER_CLASSIFICATION("POST", "/infer/classification", ClassificationInferenceResponse.class, HTTPValidationError.class, ClassificationInferenceRequest.class),

    /**
     * Runs keypoints detection inference on an image.
     * <br>Endpoint:  `/infer/keypoints_detection`
     * <br>HTTP Method:  `POST`
     * <br>Success Response:  {@link KeypointsDetectionInferenceResponse}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  {@link KeypointsDetectionInferenceRequest}
     */
    INFER_KEYPOINTS_DETECTION("POST", "/infer/keypoints_detection", KeypointsDetectionInferenceResponse.class, HTTPValidationError.class, KeypointsDetectionInferenceRequest.class),


    /**
     * Describes the interface of a predefined workflow.
     * <br>Endpoint:  `/{workspace_name}/workflows/{workflow_name}/describe_interface`
     * <br>HTTP Method:  `POST`
     * <br>Success Response:  {@link DescribeInterfaceResponse}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  {@link PredefinedWorkflowDescribeInterfaceRequest}
     */
    WORKFLOW_DESCRIBE_INTERFACE_PREDEFINED("POST", "/{workspace_name}/workflows/{workflow_name}/describe_interface", DescribeInterfaceResponse.class, HTTPValidationError.class, PredefinedWorkflowDescribeInterfaceRequest.class),

    /**
     * Describes the interface of a workflow based on its specification.
     * <br>Endpoint:  `/workflows/describe_interface`
     * <br>HTTP Method:  `POST`
     * <br>Success Response:  {@link DescribeInterfaceResponse}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  {@link WorkflowSpecificationDescribeInterfaceRequest}
     */
    WORKFLOW_DESCRIBE_INTERFACE_SPEC("POST", "/workflows/describe_interface", DescribeInterfaceResponse.class, HTTPValidationError.class, WorkflowSpecificationDescribeInterfaceRequest.class),

    /**
     * Runs a predefined workflow.
     * <br>Endpoint:  `/{workspace_name}/workflows/{workflow_name}`
     * <br>HTTP Method:  `POST`
     * <br>Success Response:  {@link WorkflowInferenceResponseDTO}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  {@link PredefinedWorkflowInferenceRequest}
     */
    WORKFLOW_RUN_PREDEFINED("POST", "/{workspace_name}/workflows/{workflow_name}", WorkflowInferenceResponseDTO.class, HTTPValidationError.class, PredefinedWorkflowInferenceRequest.class),

    /**
     * Runs a workflow based on its specification.
     * <br>Endpoint:  `/workflows/run`
     * <br>HTTP Method:  `POST`
     * <br>Success Response:  {@link WorkflowInferenceResponseDTO}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  {@link WorkflowSpecificationInferenceRequest}
     */
    WORKFLOW_RUN_SPECIFICATION("POST", "/workflows/run", WorkflowInferenceResponseDTO.class, HTTPValidationError.class, WorkflowSpecificationInferenceRequest.class),

    /**
     * Retrieves available versions of the execution engine, sorted by date.
     * <br>Endpoint:  `/workflows/execution_engine/versions`
     * <br>HTTP Method:  `GET`
     * <br>Success Response:  {@link ExecutionEngineVersions}
     * <br>Failure Response:  None
     * <br>Request Body:  None
     */
    WORKFLOW_EXECUTION_ENGINE_VERSIONS("GET", "/workflows/execution_engine/versions", ExecutionEngineVersions.class, null, null),

    /**
     * Retrieves descriptions of workflow blocks.
     * <br>Endpoint:  `/workflows/blocks/describe`
     * <br>HTTP Method:  `POST`
     * <br>Success Response:  {@link WorkflowsBlocksDescription}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  {@link WorkflowsBlocksDescriptionRequest}
     */
    WORKFLOWS_BLOCKS_DESCRIBE("POST", "/workflows/blocks/describe", WorkflowsBlocksDescription.class, HTTPValidationError.class, WorkflowsBlocksDescriptionRequest.class),

    /**
     * Retrieves the schema for workflow definitions.
     * <br>Endpoint:  `/workflows/definition/schema`
     * <br>HTTP Method:  `GET`
     * <br>Success Response:  {@link WorkflowsBlocksSchemaDescription}
     * <br>Failure Response:  None
     * <br>Request Body:  None
     */
    WORKFLOWS_DEFINITION_SCHEMA("GET", "/workflows/definition/schema", WorkflowsBlocksSchemaDescription.class, null, null),

    /**
     * Retrieves dynamic output definitions for workflow blocks.
     * <br>Endpoint:  `/workflows/blocks/dynamic_outputs`
     * <br>HTTP Method:  `POST`
     * <br>Success Response:  {@link RoboflowRequestDataArray}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  {@link AnyData}
     */
    WORKFLOWS_BLOCKS_DYNAMIC_OUTPUTS("POST", "/workflows/blocks/dynamic_outputs", RoboflowRequestDataArray.class, HTTPValidationError.class, AnyData.class),

    /**
     * Validates the JSON definition of a workflow.
     * <br>Endpoint:  `/workflows/validate`
     * <br>HTTP Method:  `POST`
     * <br>Success Response:  {@link WorkflowValidationStatus}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  {@link AnyData}
     */
    WORKFLOWS_VALIDATE("POST", "/workflows/validate", WorkflowValidationStatus.class, HTTPValidationError.class, AnyData.class),


    /**
     * Embeds an image using the CLIP model.
     * <br>Endpoint:  `/clip/embed_image`
     * <br>HTTP Method:  `POST`
     * <br>Success Response:  {@link ClipEmbeddingResponse}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  {@link ClipImageEmbeddingRequest}
     */
    CLIP_EMBED_IMAGE("POST", "/clip/embed_image", ClipEmbeddingResponse.class, HTTPValidationError.class, ClipImageEmbeddingRequest.class),

    /**
     * Embeds text using the CLIP model.
     * <br>Endpoint:  `/clip/embed_text`
     * <br>HTTP Method:  `POST`
     * <br>Success Response:  {@link ClipEmbeddingResponse}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  {@link ClipTextEmbeddingRequest}
     */
    CLIP_EMBED_TEXT("POST", "/clip/embed_text", ClipEmbeddingResponse.class, HTTPValidationError.class, ClipTextEmbeddingRequest.class),

    /**
     * Compares image and text embeddings using the CLIP model.
     * <br>Endpoint:  `/clip/compare`
     * <br>HTTP Method:  `POST`
     * <br>Success Response:  {@link ClipCompareResponse}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  {@link ClipCompareRequest}
     */
    CLIP_COMPARE("POST", "/clip/compare", ClipCompareResponse.class, HTTPValidationError.class, ClipCompareRequest.class),

    /**
     * Runs inference using the Grounding DINO model.
     * <br>Endpoint:  `/grounding_dino/infer`
     * <br>HTTP Method:  `POST`
     * <br>Success Response:  {@link ObjectDetectionInferenceResponse}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  {@link GroundingDINOInferenceRequest}
     */
    GROUNDING_DINO_INFER("POST", "/grounding_dino/infer", ObjectDetectionInferenceResponse.class, HTTPValidationError.class, GroundingDINOInferenceRequest.class),

    /**
     * Performs inference using the YOLO-World model.
     * <br>Endpoint:  `/yolo_world/infer`
     * <br>HTTP Method:  `POST`
     * <br>Success Response:  {@link ObjectDetectionInferenceResponse}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  {@link YOLOWorldInferenceRequest}
     */
    YOLO_WORLD_INFER("POST", "/yolo_world/infer", ObjectDetectionInferenceResponse.class, HTTPValidationError.class, YOLOWorldInferenceRequest.class),

    /**
     * Performs Optical Character Recognition (OCR) using the DocTR model.
     * <br>Endpoint:  `/doctr/ocr`
     * <br>HTTP Method:  `POST`
     * <br>Success Response:  {@link OCRInferenceResponse}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  {@link DoctrOCRInferenceRequest}
     */
    DOCTR_OCR("POST", "/doctr/ocr", OCRInferenceResponse.class, HTTPValidationError.class, DoctrOCRInferenceRequest.class),


    /**
     * Embeds an image using the SAM model.
     * <br>Endpoint:  `/sam/embed_image`
     * <br>HTTP Method:  `POST`
     * <br>Success Response:  {@link SamEmbeddingResponse}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  {@link SamEmbeddingRequest}
     */
    SAM_EMBED_IMAGE("POST", "/sam/embed_image", SamEmbeddingResponse.class, HTTPValidationError.class, SamEmbeddingRequest.class),

    /**
     * Segments an image using the SAM model.
     * <br>Endpoint:  `/sam/segment_image`
     * <br>HTTP Method:  `POST`
     * <br>Success Response:  {@link SamSegmentationResponse}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  {@link SamSegmentationRequest}
     */
    SAM_SEGMENT_IMAGE("POST", "/sam/segment_image", SamSegmentationResponse.class, HTTPValidationError.class, SamSegmentationRequest.class),

    /**
     * Embeds an image using the SAM2 model.
     * <br>Endpoint:  `/sam2/embed_image`
     * <br>HTTP Method:  `POST`
     * <br>Success Response:  {@link Sam2EmbeddingResponse}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  {@link Sam2EmbeddingRequest}
     */
    SAM2_EMBED_IMAGE("POST", "/sam2/embed_image", Sam2EmbeddingResponse.class, HTTPValidationError.class, Sam2EmbeddingRequest.class),

    /**
     * Segments an image using the SAM2 model.
     * <br>Endpoint:  `/sam2/segment_image`
     * <br>HTTP Method:  `POST`
     * <br>Success Response:  {@link Sam2SegmentationResponse}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  {@link Sam2SegmentationRequest}
     */
    SAM2_SEGMENT_IMAGE("POST", "/sam2/segment_image", Sam2SegmentationResponse.class, HTTPValidationError.class, Sam2SegmentationRequest.class),

    /**
     * Detects gaze points in images.
     * <br>Endpoint:  `/gaze/gaze_detection`
     * <br>HTTP Method:  `POST`
     * <br>Success Response:  {@link RoboflowRequestDataArray}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  {@link GazeDetectionInferenceRequest}
     */
    GAZE_DETECTION("POST", "/gaze/gaze_detection", RoboflowRequestDataArray.class, HTTPValidationError.class, GazeDetectionInferenceRequest.class),

    /**
     * Performs OCR using the TrOCR model.
     * <br>Endpoint:  `/ocr/trocr`
     * <br>HTTP Method:  `POST`
     * <br>Success Response:  {@link OCRInferenceResponse}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  {@link TrOCRInferenceRequest}
     */
    TROCR_OCR("POST", "/ocr/trocr", OCRInferenceResponse.class, HTTPValidationError.class, TrOCRInferenceRequest.class),

    /**
     * Starts a notebook session.
     * <br>Endpoint:  `/notebook/start`
     * <br>HTTP Method:  `GET`
     * <br>Success Response:  {@link AnyData}
     * <br>Failure Response:  {@link HTTPValidationError}
     * <br>Request Body:  None
     */
    NOTEBOOK_START("GET", "/notebook/start", AnyData.class, HTTPValidationError.class, null);

    /**
     * HTTP method for the request (e.g., "GET" or "POST").
     */
    private final String method;

    /**
     * Base URI path for the API endpoint.
     */
    private final String BASE_URI;

    /**
     * Class representing the expected successful response data type.
     */
    private final Class<? extends RoboflowResponseData> success;

    /**
     * Class representing the expected failure response data type.
     */
    private final Class<? extends RoboflowResponseData> failure;

    /**
     * Class representing the type of the request body data.
     */
    private final Class<? extends RoboflowRequestData> requestBodyClass;

    /**
     * Constructs a {@code RoboflowRequest} enum constant with the specified details.
     *
     * @param method           the HTTP method for the request
     * @param baseUri         the base URI path for the API endpoint
     * @param success         the class representing the successful response data type
     * @param failure         the class representing the failure response data type
     * @param requestBodyClass the class representing the request body data type
     */
    RoboflowRequest(String method,
                    String baseUri,
                    Class<? extends RoboflowResponseData> success,
                    Class<? extends RoboflowResponseData> failure,
                    Class<? extends RoboflowRequestData> requestBodyClass) {
        this.method = method;
        this.BASE_URI = baseUri;
        this.success = success;
        this.failure = failure;
        this.requestBodyClass = requestBodyClass;
    }

    /**
     * Sends an asynchronous request with the specified data and configuration.
     *
     * @param requestSender   the {@code RequestSender} instance to handle the request
     * @param data           the data to include in the request body
     * @param roboflowConfig the configuration for the Roboflow API
     * @return a {@code CompletableFuture} that will complete with the response data
     */
    public CompletableFuture<RoboflowResponseData> sendAsync(RequestSender requestSender, RoboflowRequestData data, RoboflowConfig roboflowConfig) {
        try {
            URI uri = new URI(roboflowConfig.getHost() + BASE_URI);
            if (method.equals("GET")) {
                return requestSender.getAsync(uri, log, success, failure);
            } else {
                return requestSender.postAsync(uri, log, data, success, failure, requestBodyClass);
            }
        } catch (URISyntaxException e) {
            return CompletableFuture.completedFuture(null);
        }
    }

    /**
     * Sends an asynchronous request without a request body and with the specified configuration.
     *
     * @param requestSender   the {@code RequestSender} instance to handle the request
     * @param roboflowConfig the configuration for the Roboflow API
     * @return a {@code CompletableFuture} that will complete with the response data
     */
    public CompletableFuture<RoboflowResponseData> sendAsync(RequestSender requestSender, RoboflowConfig roboflowConfig) {
        return sendAsync(requestSender, null, roboflowConfig);
    }

    /**
     * Sends a synchronous request with the specified data, waiting up to the given timeout.
     *
     * @param requestSender   the {@code RequestSender} instance to handle the request
     * @param data           the data to include in the request body
     * @param timeout        the maximum time to wait for the response
     * @param unit           the time unit of the timeout
     * @param roboflowConfig the configuration for the Roboflow API
     * @return the response data
     * @throws ExecutionException if the request execution encounters an error
     * @throws InterruptedException if the current thread is interrupted while waiting
     * @throws TimeoutException if the wait time elapses before a response is received
     */
    public RoboflowResponseData send(RequestSender requestSender, RoboflowRequestData data, long timeout, TimeUnit unit, RoboflowConfig roboflowConfig)
            throws ExecutionException, InterruptedException, TimeoutException {
        return sendAsync(requestSender, data, roboflowConfig).get(timeout, unit);
    }

    /**
     * Sends a synchronous request with the specified data.
     *
     * @param requestSender   the {@code RequestSender} instance to handle the request
     * @param data           the data to include in the request body
     * @param roboflowConfig the configuration for the Roboflow API
     * @return the response data
     * @throws ExecutionException if the request execution encounters an error
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    public RoboflowResponseData send(RequestSender requestSender, RoboflowRequestData data, RoboflowConfig roboflowConfig)
            throws ExecutionException, InterruptedException {
        return sendAsync(requestSender, data, roboflowConfig).get();
    }

    /**
     * Sends a synchronous request without a request body.
     *
     * @param requestSender   the {@code RequestSender} instance to handle the request
     * @param roboflowConfig the configuration for the Roboflow API
     * @return the response data
     * @throws ExecutionException if the request execution encounters an error
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    public RoboflowResponseData send(RequestSender requestSender, RoboflowConfig roboflowConfig)
            throws ExecutionException, InterruptedException {
        return sendAsync(requestSender, roboflowConfig).get();
    }

    /**
     * Sends an asynchronous request with the specified data, configuration, and path variables. The path variables will be
     * used to replace placeholders in the BASE_URI.
     *
     * @param requestSender   the {@code RequestSender} instance to handle the request
     * @param data           the data to include in the request body
     * @param roboflowConfig the configuration for the Roboflow API
     * @param pathVar        a map of path variables to replace placeholders in the BASE_URI
     * @return a {@code CompletableFuture} that will complete with the response data
     * @throws IllegalArgumentException if the BASE_URI contains unprocessed placeholders (e.g., "{*}")
     */
    public CompletableFuture<RoboflowResponseData> sendAsync(RequestSender requestSender, RoboflowRequestData data,
                                                             RoboflowConfig roboflowConfig, Map<String, String> pathVar) {
        try {
            URI uri = buildUriWithPathVars(roboflowConfig.getHost() + BASE_URI, pathVar);
            if (method.equals("GET")) {
                return requestSender.getAsync(uri, log, success, failure);
            } else {
                return requestSender.postAsync(uri, log, data, success, failure, requestBodyClass);
            }
        } catch (URISyntaxException | IllegalArgumentException e) {
            return CompletableFuture.completedFuture(null);
        }
    }

    /**
     * Sends an asynchronous request without a request body and with the specified configuration and path variables.
     * The path variables will be used to replace placeholders in the BASE_URI.
     *
     * @param requestSender   the {@code RequestSender} instance to handle the request
     * @param roboflowConfig the configuration for the Roboflow API
     * @param pathVar        a map of path variables to replace placeholders in the BASE_URI
     * @return a {@code CompletableFuture} that will complete with the response data
     * @throws IllegalArgumentException if the BASE_URI contains unprocessed placeholders (e.g., "{*}")
     */
    public CompletableFuture<RoboflowResponseData> sendAsync(RequestSender requestSender, RoboflowConfig roboflowConfig,
                                                             Map<String, String> pathVar) {
        return sendAsync(requestSender, null, roboflowConfig, pathVar);
    }

    /**
     * Sends a synchronous request with the specified data, waiting up to the given timeout, using the provided path variables
     * to replace placeholders in the BASE_URI.
     *
     * @param requestSender   the {@code RequestSender} instance to handle the request
     * @param data           the data to include in the request body
     * @param timeout        the maximum time to wait for the response
     * @param unit           the time unit of the timeout
     * @param roboflowConfig the configuration for the Roboflow API
     * @param pathVar        a map of path variables to replace placeholders in the BASE_URI
     * @return the response data
     * @throws ExecutionException if the request execution encounters an error
     * @throws InterruptedException if the current thread is interrupted while waiting
     * @throws TimeoutException if the wait time elapses before a response is received
     * @throws IllegalArgumentException if the BASE_URI contains unprocessed placeholders (e.g., "{*}")
     */
    public RoboflowResponseData send(RequestSender requestSender, RoboflowRequestData data, long timeout, TimeUnit unit,
                                     RoboflowConfig roboflowConfig, Map<String, String> pathVar)
            throws ExecutionException, InterruptedException, TimeoutException {
        return sendAsync(requestSender, data, roboflowConfig, pathVar).get(timeout, unit);
    }

    /**
     * Sends a synchronous request with the specified data, using the provided path variables to replace placeholders in
     * the BASE_URI.
     *
     * @param requestSender   the {@code RequestSender} instance to handle the request
     * @param data           the data to include in the request body
     * @param roboflowConfig the configuration for the Roboflow API
     * @param pathVar        a map of path variables to replace placeholders in the BASE_URI
     * @return the response data
     * @throws ExecutionException if the request execution encounters an error
     * @throws InterruptedException if the current thread is interrupted while waiting
     * @throws IllegalArgumentException if the BASE_URI contains unprocessed placeholders (e.g., "{*}")
     */
    public RoboflowResponseData send(RequestSender requestSender, RoboflowRequestData data, RoboflowConfig roboflowConfig,
                                     Map<String, String> pathVar)
            throws ExecutionException, InterruptedException {
        return sendAsync(requestSender, data, roboflowConfig, pathVar).get();
    }

    /**
     * Sends a synchronous request without a request body, using the provided path variables to replace placeholders in
     * the BASE_URI.
     *
     * @param requestSender   the {@code RequestSender} instance to handle the request
     * @param roboflowConfig the configuration for the Roboflow API
     * @param pathVar        a map of path variables to replace placeholders in the BASE_URI
     * @return the response data
     * @throws ExecutionException if the request execution encounters an error
     * @throws InterruptedException if the current thread is interrupted while waiting
     * @throws IllegalArgumentException if the BASE_URI contains unprocessed placeholders (e.g., "{*}")
     */
    public RoboflowResponseData send(RequestSender requestSender, RoboflowConfig roboflowConfig, Map<String, String> pathVar)
            throws ExecutionException, InterruptedException {
        return sendAsync(requestSender, roboflowConfig, pathVar).get();
    }

    /**
     * Builds a URI by replacing path variables in the given URI template with the corresponding values in the provided map.
     * If there are any unprocessed placeholders like "{*}", an {@code IllegalArgumentException} will be thrown.
     *
     * @param uriTemplate the URI template containing placeholders
     * @param pathVar     a map of path variables to replace placeholders in the URI template
     * @return the constructed URI with replaced path variables
     * @throws URISyntaxException if the URI template is invalid or contains unprocessed placeholders
     * @throws IllegalArgumentException if the URI template contains unprocessed placeholders (e.g., "{*}")
     */
    private URI buildUriWithPathVars(String uriTemplate, Map<String, String> pathVar) throws URISyntaxException {
        String resultUri = uriTemplate;
        if (pathVar.containsKey("host") && pathVar.containsKey("port")) {
            resultUri = pathVar.get("host") + ':' + pathVar.get("port");
        }

        for (Map.Entry<String, String> entry : pathVar.entrySet()) {
            resultUri = resultUri.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        // Check if there are any remaining placeholders like "{}"
        if (resultUri.contains("{") || resultUri.contains("}")) {
            throw new IllegalArgumentException("URI contains unprocessed placeholder: {*}");
        }

        log.debug("URI result with path input : {}", resultUri);
        return new URI(resultUri);
    }

}
