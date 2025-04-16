package nusri.fyp.demo.service.img_sender.roboflow;

import lombok.extern.slf4j.Slf4j;
import nusri.fyp.demo.roboflow.data.entity.workflow.dto.WorkflowInferenceResponseDTO;
import nusri.fyp.demo.entity.ActionWithId;
import nusri.fyp.demo.entity.ObjectWithId;
import nusri.fyp.demo.repository.ActionRepository;
import nusri.fyp.demo.repository.ObjectRepository;
import nusri.fyp.demo.roboflow.RoboflowConfig;
import nusri.fyp.demo.roboflow.data.RoboflowResponseData;
import nusri.fyp.demo.roboflow.data.entity.InferenceImageDimensions;
import nusri.fyp.demo.roboflow.data.entity.InferenceRequestImage;
import nusri.fyp.demo.roboflow.data.entity.workflow.SinglePrediction;
import nusri.fyp.demo.roboflow.data.entity.workflow.WorkflowOutputData;
import nusri.fyp.demo.roboflow.data.request.PredefinedWorkflowDescribeInterfaceRequest;
import nusri.fyp.demo.roboflow.data.request.PredefinedWorkflowInferenceRequest;
import nusri.fyp.demo.roboflow.data.response.*;
import nusri.fyp.demo.roboflow.request.RequestSenderOfOKHttp;
import nusri.fyp.demo.state_machine.AbstractActionObservation;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static nusri.fyp.demo.roboflow.request.RoboflowRequest.*;

/**
 * <b>Service class for interacting with Roboflow-based workflows, including synchronous and asynchronous image processing.</b>
 * <br> This class handles:
 * <ul>
 *   <li>Sending Base64-encoded images to the Roboflow API for inference.</li>
 *   <li>Processing responses to map predicted classes to appropriate {@link ActionWithId} or {@link ObjectWithId} entities.</li>
 *   <li>Storing predictions as {@link SinglePrediction} (or {@link AbstractActionObservation} for generalized usage).</li>
 *   <li>Testing pre-defined workflows via request calls, including describing the interface and running inferences.</li>
 * </ul>
 * <br>
 * Key points:
 * <br> &bull; Synchronous operations: {@link #sendImg(String, String, String, String)} immediately returns a list of predictions.<br>
 * &bull; Asynchronous operations: {@link #sendImgAsync(String, String, String, String)} returns a {@link CompletableFuture} that completes once Roboflow returns its results.<br>
 * &bull; Internal test methods (e.g., {@code testPredefineWorkflowOnImage()}) demonstrate usage of the Roboflow API for debugging or sample calls.<br>
 * <br>
 * Depends on:
 * <br> &bull; {@link RoboflowConfig} for storing API credentials and relevant workflow information.<br>
 * &bull; {@link RequestSenderOfOKHttp} for sending HTTP requests to Roboflow.<br>
 * &bull; {@link ObjectRepository} and {@link ActionRepository} for looking up object/action names from the predicted class IDs.<br>
 *
 * @see SinglePrediction
 * @see AbstractActionObservation
 * @see WorkflowInferenceResponseDTO
 * @see RoboflowResponseData
 * @see WorkflowOutputData
 * @see RoboflowConfig
 * @see RequestSenderOfOKHttp
 * @see ObjectRepository
 * @see ActionRepository
 */
@Service
@Slf4j
public class RoboflowService {

    private final RequestSenderOfOKHttp requestSenderOfOKHttp;
    private final ObjectRepository objectRepository;
    private final ActionRepository actionRepository;
    private final RoboflowConfig roboflowConfig;

    /**
     * Constructs the {@link RoboflowService} with required dependencies.
     * <br> This includes HTTP request handling, repositories for object/action lookups, and the Roboflow configuration.
     * <br> On initialization, it attempts to call several test routes (e.g., {@link #testPredefineWorkflow(RequestSenderOfOKHttp, RoboflowConfig, String, String)})
     * to confirm API availability and log relevant data.
     *
     * @param requestSenderOfOKHttp The HTTP request sender for Roboflow API requests
     * @param objectRepository The repository used for mapping object IDs to their names
     * @param actionRepository The repository used for mapping action IDs to their names
     * @param roboflowConfig The configuration containing API credentials and settings for Roboflow
     * @see RoboflowConfig
     * @see RequestSenderOfOKHttp
     */
    RoboflowService(RequestSenderOfOKHttp requestSenderOfOKHttp,
                    ObjectRepository objectRepository,
                    ActionRepository actionRepository,
                    RoboflowConfig roboflowConfig) {
        this.requestSenderOfOKHttp = requestSenderOfOKHttp;
        this.objectRepository = objectRepository;
        this.actionRepository = actionRepository;
        this.roboflowConfig = roboflowConfig;

        try {
            // Log available workflow execution engine versions
            log.info("Roboflow WORKFLOW_EXECUTION_ENGINE_VERSIONS: {}",
                    WORKFLOW_EXECUTION_ENGINE_VERSIONS.send(requestSenderOfOKHttp, roboflowConfig).toString());

            // Test a pre-defined workflow describing interface
            RoboflowResponseData send = testPredefineWorkflow(requestSenderOfOKHttp, roboflowConfig,
                    "tomcai", "detect-count-and-visualize-2");
            log.debug("Roboflow WORKFLOW_DESCRIBE_INTERFACE_PREDEFINED : {}", send);

            // Test an actual image workflow run
            testPredefineWorkflowOnImage(requestSenderOfOKHttp, roboflowConfig);

        } catch (ExecutionException e) {
            log.error("Roboflow Execution exception", e);
        } catch (InterruptedException e) {
            log.error("Roboflow Execution engine versions getting interrupted");
        } catch (IOException e) {
            log.error("Roboflow IO exception", e);
        }
    }

    /**
     * Synchronously sends a Base64-encoded image to Roboflow for inference.
     * <br> If successful, the predictions are mapped to {@link SinglePrediction} objects, with class IDs translated to
     * human-readable names via {@link #objectRepository} or {@link #actionRepository}.
     *
     * @param base64String The Base64-encoded image to be sent to Roboflow
     * @param workspace_name The workspace name used in the Roboflow API request
     * @param workflow_name The workflow name used in the Roboflow API request
     * @param workflow_id The specific workflow ID used for inference
     * @return A list of {@link SinglePrediction} objects extracted from Roboflow's response
     * @see #runPredefineWorkflowOnImage(RequestSenderOfOKHttp, RoboflowConfig, String, String, String, String)
     * @see #objectRepository
     * @see #actionRepository
     */
    public List<SinglePrediction> sendImg(String base64String,
                                          String workspace_name,
                                          String workflow_name,
                                          String workflow_id) {
        try {
            // Perform the synchronous request
            RoboflowResponseData send = runPredefineWorkflowOnImage(requestSenderOfOKHttp,
                    roboflowConfig, workspace_name, workflow_name, base64String, workflow_id);

            // Handle potential errors
            if (send instanceof HTTPValidationError) {
                log.error("HTTPValidationError when sync sending img:: {}", send);
            }

            // If valid response, process predictions
            if (send instanceof WorkflowInferenceResponseDTO response) {
                // We expect only one output in typical usage
                WorkflowOutputData workflowOutputData = response.toEntity().getOutputs().get(0);

                // Save the output image locally (for debug/auditing)
                workflowOutputData.getOutputImage().saveToDir("D:\\桌面文件\\serverSrc\\proceed_images\\");

                // Process predictions
                List<SinglePrediction> predictions = workflowOutputData.getPredictions().getPredictions();
                predictions = predictions.stream().peek(singlePrediction -> {
                    String clazz = singlePrediction.getClazz().toLowerCase();
                    singlePrediction.setLabel(clazz);

                    // If the class is "object" or "action" followed by an ID, map it from the DB
                    if (clazz.startsWith("object")) {
                        String objectStr = clazz.replace("object", "");
                        if (objectStr.startsWith(" ")) {
                            objectStr = objectStr.substring(1);
                        }
                        Optional<ObjectWithId> object = objectRepository.findById(Integer.parseInt(objectStr));
                        object.ifPresent(objectWithId -> singlePrediction.setClazz(objectWithId.getName()));
                    } else if (clazz.startsWith("action")) {
                        String objectStr = clazz.replace("action", "");
                        if (objectStr.startsWith(" ")) {
                            objectStr = objectStr.substring(1);
                        }
                        Optional<ActionWithId> action = actionRepository.findById(Integer.parseInt(objectStr));
                        action.ifPresent(actionWithId -> singlePrediction.setClazz(actionWithId.getName()));
                    }
                }).toList();

                log.info("predictions: {}", predictions);
                return predictions;
            }

        } catch (ExecutionException | InterruptedException | IOException e) {
            log.error(e.getMessage());
        }
        log.error("sendImg exception");
        return new ArrayList<>();
    }

    /**
     * Asynchronously sends a Base64-encoded image to Roboflow for inference.
     * <br> Returns a {@link CompletableFuture} that completes once Roboflow responds with predictions, which are then
     * converted into {@link AbstractActionObservation} objects for broader state machine compatibility.
     *
     * @param base64String The Base64-encoded image to be sent to Roboflow
     * @param workspace_name The workspace name used in the Roboflow API request
     * @param workflow_name The workflow name used in the Roboflow API request
     * @param workflow_id The specific workflow ID used for inference
     * @return a {@link CompletableFuture} holding a list of {@link AbstractActionObservation} after inference
     * @see #runPredefineWorkflowOnImageAsync(RequestSenderOfOKHttp, RoboflowConfig, String, String, String, String)
     * @see #objectRepository
     * @see #actionRepository
     */
    public CompletableFuture<List<AbstractActionObservation>> sendImgAsync(String base64String,
                                                                           String workspace_name,
                                                                           String workflow_name,
                                                                           String workflow_id) {
        CompletableFuture<RoboflowResponseData> completableFuture;
        try {
            // Kick off the async request
            completableFuture = runPredefineWorkflowOnImageAsync(
                    requestSenderOfOKHttp, roboflowConfig, workspace_name, workflow_name, base64String, workflow_id);
        } catch (IOException | ExecutionException | InterruptedException e) {
            // In case of any error upfront, return a completed future with empty data
            log.error(e.getMessage());
            CompletableFuture<List<AbstractActionObservation>> completableFutureEx = new CompletableFuture<>();
            completableFutureEx.complete(new ArrayList<>());
            return completableFutureEx;
        }

        // Process the asynchronous Roboflow response
        return completableFuture.thenApply(send -> {
            if (send instanceof HTTPValidationError) {
                log.error("HTTPValidationError when async sending img: {}", send);
            }

            if (send instanceof WorkflowInferenceResponseDTO response) {
                // For a single-image workflow, there's typically only one output
                if (response.toEntity().getOutputs().size() != 1) {
                    log.error("Error: Too many outputs: {}", response.toEntity().getOutputs());
                }

                WorkflowOutputData workflowOutputData = response.toEntity().getOutputs().get(0);

                // Save the output image locally for reference
                workflowOutputData.getOutputImage().saveToDir("D:\\桌面文件\\serverSrc\\proceed_images\\");

                // Convert Roboflow predictions to a list of AbstractActionObservation
                return workflowOutputData.getPredictions().getPredictions().stream().map(singlePrediction -> {
                    String clazz = singlePrediction.getClazz().toLowerCase();
                    singlePrediction.setLabel(clazz);

                    if (clazz.startsWith("object")) {
                        String objectStr = clazz.replace("object", "");
                        if (objectStr.startsWith(" ")) {
                            objectStr = objectStr.substring(1);
                        }
                        Optional<ObjectWithId> object = objectRepository.findById(Integer.parseInt(objectStr));
                        object.ifPresent(objectWithId -> singlePrediction.setClazz(objectWithId.getName()));
                    } else if (clazz.startsWith("action")) {
                        String objectStr = clazz.replace("action", "");
                        if (objectStr.startsWith(" ")) {
                            objectStr = objectStr.substring(1);
                        }
                        Optional<ActionWithId> action = actionRepository.findById(Integer.parseInt(objectStr));
                        action.ifPresent(actionWithId -> singlePrediction.setClazz(actionWithId.getName()));
                    }
                    return (AbstractActionObservation) singlePrediction;
                }).toList();
            }

            // If we get here, something went wrong or no predictions
            return new ArrayList<>();
        });
    }

    /**
     * Executes a pre-defined workflow for inference on a single image, synchronously.
     * <br> This method builds the {@link PredefinedWorkflowInferenceRequest} and sends the request.
     * <br> It's typically used internally by {@link #sendImg(String, String, String, String)}.
     *
     * @param requestSenderOfOKHttp the custom HTTP sender
     * @param roboflowConfig the Roboflow API configuration
     * @param workspace_name the workspace name
     * @param workflow_name the workflow name
     * @param base64 the base64-encoded image
     * @param workflowId the workflow ID
     * @return a {@link RoboflowResponseData} containing Roboflow's response
     * @throws IOException if I/O fails
     * @throws ExecutionException if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted
     * @see PredefinedWorkflowInferenceRequest
     */
    private RoboflowResponseData runPredefineWorkflowOnImage(RequestSenderOfOKHttp requestSenderOfOKHttp,
                                                             RoboflowConfig roboflowConfig,
                                                             String workspace_name,
                                                             String workflow_name,
                                                             String base64,
                                                             String workflowId)
            throws IOException, ExecutionException, InterruptedException {

        PredefinedWorkflowInferenceRequest data3 = getPredefinedWorkflowInferenceRequest(roboflowConfig, base64, workflowId);
        return WORKFLOW_RUN_PREDEFINED.send(requestSenderOfOKHttp,
                data3,
                roboflowConfig,
                buildWorkflowPathMap(workspace_name, workflow_name));
    }

    /**
     * Executes a pre-defined workflow for inference on a single image, asynchronously.
     * <br> This method returns a {@link CompletableFuture} enabling non-blocking calls.
     *
     * @param requestSenderOfOKHttp the custom HTTP sender
     * @param roboflowConfig the Roboflow API configuration
     * @param workspace_name the workspace name
     * @param workflow_name the workflow name
     * @param base64 the base64-encoded image
     * @param workflowId the workflow ID
     * @return a future {@link RoboflowResponseData} upon completion
     * @throws IOException if I/O fails
     * @throws ExecutionException if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted
     * @see PredefinedWorkflowInferenceRequest
     */
    private CompletableFuture<RoboflowResponseData> runPredefineWorkflowOnImageAsync(RequestSenderOfOKHttp requestSenderOfOKHttp,
                                                                                     RoboflowConfig roboflowConfig,
                                                                                     String workspace_name,
                                                                                     String workflow_name,
                                                                                     String base64,
                                                                                     String workflowId)
            throws IOException, ExecutionException, InterruptedException {

        PredefinedWorkflowInferenceRequest data3 = getPredefinedWorkflowInferenceRequest(roboflowConfig, base64, workflowId);
        return WORKFLOW_RUN_PREDEFINED.sendAsync(requestSenderOfOKHttp,
                data3,
                roboflowConfig,
                buildWorkflowPathMap(workspace_name, workflow_name));
    }

    /**
     * Constructs a {@link PredefinedWorkflowInferenceRequest} object using base64 image data and a target workflow ID.
     * <br> This object is then used in Roboflow inference calls.
     *
     * @param roboflowConfig the Roboflow configuration
     * @param base64 the base64-encoded image
     * @param workflowId the workflow ID
     * @return a fully populated {@link PredefinedWorkflowInferenceRequest} ready for inference
     */
    private PredefinedWorkflowInferenceRequest getPredefinedWorkflowInferenceRequest(RoboflowConfig roboflowConfig,
                                                                                     String base64,
                                                                                     String workflowId) {
        PredefinedWorkflowInferenceRequest data3 = new PredefinedWorkflowInferenceRequest();
        data3.setApiKey(roboflowConfig.getApiKey());
        data3.setWorkflowId(workflowId);
        data3.setUseCache(false);
        data3.setExcludedFields(new ArrayList<>());
        data3.setEnableProfiling(false);

        // Build the image payload
        InferenceRequestImage image = new InferenceRequestImage();
        image.setType("base64");
        image.setValue(base64);
        log.debug("test image size: {}", base64.length());
        image.setPath("test2.png");
        image.setPrefix("data:image/png;base64,");
        image.setNewDimensions(new InferenceImageDimensions(2048, 1114));
        image.setOriginalDimensions(new InferenceImageDimensions(2560, 1392));
        image.setResized(false);

        // Attach image to the request
        data3.addImage(image);
        return data3;
    }

    /**
     * Tests inference by loading a local file, converting it to Base64, and running the workflow.
     * <br> This is primarily used for debugging and demonstration purposes.
     *
     * @param requestSenderOfOKHttp the HTTP sender
     * @param roboflowConfig the Roboflow config
     * @throws IOException if file operations fail
     * @throws ExecutionException if the Roboflow request fails in an unexpected way
     * @throws InterruptedException if the thread is interrupted
     * @see #runPredefineWorkflowOnImage(RequestSenderOfOKHttp, RoboflowConfig, String, String, String, String)
     */
    private void testPredefineWorkflowOnImage(RequestSenderOfOKHttp requestSenderOfOKHttp,
                                              RoboflowConfig roboflowConfig)
            throws IOException, ExecutionException, InterruptedException {

        Path path = Paths.get("C:\\Users\\dell\\Desktop\\test2.png");
        byte[] imageBytes = Files.readAllBytes(path);
        String base64String = Base64.getEncoder().encodeToString(imageBytes);

        log.info("Roboflow WORKFLOW_RUN_PREDEFINED TEST: {}",
                runPredefineWorkflowOnImage(requestSenderOfOKHttp,
                        roboflowConfig,
                        "tomcai",
                        "detect-count-and-visualize-2",
                        base64String,
                        "KVPLmLosVn1uvCCTbCfq"));
    }

    /**
     * Builds a map of path variables for Roboflow workflows, ensuring workspace and workflow names are formatted properly.
     * <br> This helps handle potential issues with spaces, commas, or case sensitivity.
     *
     * @param workspace_name the workspace name
     * @param workflow_name the workflow name
     * @return a map of path variables to be inserted into the Roboflow API URL
     */
    private Map<String, String> buildWorkflowPathMap(String workspace_name, String workflow_name) {
        workflow_name = workflow_name.replace(" ", "-");
        workspace_name = workspace_name.replace(" ", "-");
        workflow_name = workflow_name.toLowerCase();
        workspace_name = workspace_name.toLowerCase();

        Map<String, String> pathValues = new HashMap<>();
        pathValues.put("workspace_name", workspace_name);
        pathValues.put("workflow_name", workflow_name);
        return pathValues;
    }

    /**
     * Tests the pre-defined workflow interface on Roboflow by sending a {@link PredefinedWorkflowDescribeInterfaceRequest}.
     * <br> Logs the response for debugging purposes.
     *
     * @param requestSenderOfOKHttp the HTTP request sender
     * @param roboflowConfig the Roboflow configuration
     * @param workspace_name the workspace name
     * @param workflow_name the workflow name
     * @return a {@link RoboflowResponseData} with the workflow interface description
     * @throws ExecutionException if the request fails unexpectedly
     * @throws InterruptedException if the thread is interrupted
     * @see PredefinedWorkflowDescribeInterfaceRequest
     */
    private RoboflowResponseData testPredefineWorkflow(RequestSenderOfOKHttp requestSenderOfOKHttp,
                                                       RoboflowConfig roboflowConfig,
                                                       String workspace_name,
                                                       String workflow_name)
            throws ExecutionException, InterruptedException {

        buildWorkflowPathMap(workspace_name, workflow_name);
        PredefinedWorkflowDescribeInterfaceRequest data = new PredefinedWorkflowDescribeInterfaceRequest();
        data.setApiKey(roboflowConfig.getApiKey());
        data.setUseCache(false);

        return WORKFLOW_DESCRIBE_INTERFACE_PREDEFINED.send(requestSenderOfOKHttp,
                data,
                roboflowConfig,
                buildWorkflowPathMap(workspace_name, workflow_name));
    }

    /**
     * Test connection of a specific host and port by using Roboflow API.
     *
     * @param host host.
     * @param port port.
     * @throws ExecutionException throw then test fail.
     * @throws InterruptedException throw when test is interrupted.
     */
    public void test(String host, String port) throws ExecutionException, InterruptedException {
        Map<String, String> pathValues = new HashMap<>();
        pathValues.put("host", host);
        pathValues.put("port", port);
        WORKFLOW_EXECUTION_ENGINE_VERSIONS.send(requestSenderOfOKHttp, roboflowConfig, pathValues);
    }
}
