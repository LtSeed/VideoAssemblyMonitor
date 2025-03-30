package nusri.fyp.demo.service.img_sender.roboflow;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import nusri.fyp.demo.roboflow.request.RoboflowRequest;
import nusri.fyp.demo.state_machine.AbstractActionObservation;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static nusri.fyp.demo.roboflow.request.RoboflowRequest.*;

/**
 * Service that encapsulates the logic for making inference requests to the Roboflow platform, including both synchronous
 * and asynchronous calls for image processing.
 * <br> This service sends Base64-encoded images to Roboflow, receives prediction results, and processes the results
 * (including visualizations and object counting).
 * Key Points:
 * <ul>
 *    <li>Synchronous and Asynchronous Processing: <br>
 *        The service supports both blocking (synchronous) and non-blocking
 *        (asynchronous) image processing using Roboflow.</li>
 *    <li>Prediction Handling: <br>
 *        The predictions returned by Roboflow are processed, mapped to class names from the database,
 *        and returned as a list of SinglePrediction objects.</li>
 * </ul>
 */
@Service
@Slf4j
public class RoboflowService {

    private final RequestSenderOfOKHttp requestSenderOfOKHttp;
    private final ObjectRepository objectRepository;
    private final ActionRepository actionRepository;
    private final RoboflowConfig roboflowConfig;

    /**
     * Constructor to inject necessary dependencies such as the request sender, object/action repositories, and Roboflow configuration.
     *
     * @param requestSenderOfOKHttp The HTTP request sender for Roboflow API requests.
     * @param objectRepository The repository used for object-to-id mapping.
     * @param actionRepository The repository used for action-to-id mapping.
     * @param roboflowConfig The configuration containing API credentials and settings for Roboflow.
     * @param objectMapper The object mapper.
     */
    RoboflowService(RequestSenderOfOKHttp requestSenderOfOKHttp,
                    ObjectRepository objectRepository,
                    ActionRepository actionRepository,
                    RoboflowConfig roboflowConfig, ObjectMapper objectMapper) {
        this.requestSenderOfOKHttp = requestSenderOfOKHttp;
        this.objectRepository = objectRepository;
        this.actionRepository = actionRepository;
        this.roboflowConfig = roboflowConfig;

        try {
            log.info("Roboflow WORKFLOW_EXECUTION_ENGINE_VERSIONS: {}", WORKFLOW_EXECUTION_ENGINE_VERSIONS.send(requestSenderOfOKHttp, roboflowConfig).toString());

            RoboflowResponseData send = testPredefineWorkflow(requestSenderOfOKHttp, roboflowConfig, "tomcai", "detect-count-and-visualize-2");
            log.debug("Roboflow WORKFLOW_DESCRIBE_INTERFACE_PREDEFINED : {}", send);

            testPredefineWorkflowOnImage(requestSenderOfOKHttp, roboflowConfig);

        } catch (ExecutionException e) {
            log.error("Roboflow Execution exception", e);
        } catch (InterruptedException e) {
            log.error("Roboflow Execution engine versions getting interrupted");
        } catch (IOException e) {
            log.error("Roboflow IO exception", e);
        }
    }


    private RoboflowResponseData runPredefineWorkflowOnImage(RequestSenderOfOKHttp requestSenderOfOKHttp, RoboflowConfig roboflowConfig, String workspace_name, String workflow_name, String base64, String workflowId) throws IOException, ExecutionException, InterruptedException {

        PredefinedWorkflowInferenceRequest data3 = getPredefinedWorkflowInferenceRequest(roboflowConfig, base64, workflowId);

        return WORKFLOW_RUN_PREDEFINED.send(requestSenderOfOKHttp,
                data3,
                roboflowConfig,
                buildWorkflowPathMap(workspace_name, workflow_name));
    }

    private CompletableFuture<RoboflowResponseData> runPredefineWorkflowOnImageAsync(RequestSenderOfOKHttp requestSenderOfOKHttp, RoboflowConfig roboflowConfig, String workspace_name, String workflow_name, String base64, String workflowId) throws IOException, ExecutionException, InterruptedException {

        PredefinedWorkflowInferenceRequest data3 = getPredefinedWorkflowInferenceRequest(roboflowConfig, base64, workflowId);

        return WORKFLOW_RUN_PREDEFINED.sendAsync(requestSenderOfOKHttp,
                data3,
                roboflowConfig,
                buildWorkflowPathMap(workspace_name, workflow_name));
    }

    private PredefinedWorkflowInferenceRequest getPredefinedWorkflowInferenceRequest(RoboflowConfig roboflowConfig, String base64, String workflowId) {
        PredefinedWorkflowInferenceRequest data3 = new PredefinedWorkflowInferenceRequest();
        data3.setApiKey(roboflowConfig.getApiKey());
        data3.setWorkflowId(workflowId);
        data3.setUseCache(false);
        data3.setExcludedFields(new ArrayList<>());
        data3.setEnableProfiling(false);
        InferenceRequestImage image = new InferenceRequestImage();

        image.setType("base64");
        image.setValue(base64);
        log.info("test image size: {}", base64.length());
        image.setPath("test2.png");
        image.setPrefix("data:image/png;base64,");
        image.setNewDimensions(new InferenceImageDimensions(2048, 1114));
        image.setOriginalDimensions(new InferenceImageDimensions(2560, 1392));
        image.setResized(false);
        data3.addImage(image);
        return data3;
    }


    private void testPredefineWorkflowOnImage(RequestSenderOfOKHttp requestSenderOfOKHttp, RoboflowConfig roboflowConfig) throws IOException, ExecutionException, InterruptedException {

        Path path = Paths.get("C:\\Users\\dell\\Desktop\\test2.png");
        byte[] imageBytes = Files.readAllBytes(path);
        String base64String = Base64.getEncoder().encodeToString(imageBytes);

        log.info("Roboflow WORKFLOW_RUN_PREDEFINED TEST: {}", runPredefineWorkflowOnImage(requestSenderOfOKHttp,
                roboflowConfig,
                "tomcai",
                "detect-count-and-visualize-2",
                base64String, "KVPLmLosVn1uvCCTbCfq"));
    }

    private Map<String, String> buildWorkflowPathMap(String workspace_name, String workflow_name) {
        workflow_name =  workflow_name.replace(" ", "-");
        workspace_name = workspace_name.replace(" ", "-");
        workflow_name =  workflow_name.replace("，", "");
        workspace_name = workspace_name.replace("，", "");
        workflow_name = workflow_name.toLowerCase();
        workspace_name = workspace_name.toLowerCase();
        Map<String, String> pathValues = new HashMap<>();
        pathValues.put("workspace_name", workspace_name);
        pathValues.put("workflow_name", workflow_name);
        return pathValues;
    }

    private RoboflowResponseData testPredefineWorkflow(RequestSenderOfOKHttp requestSenderOfOKHttp, RoboflowConfig roboflowConfig, String workspace_name, String workflow_name) throws ExecutionException, InterruptedException {
        buildWorkflowPathMap(workspace_name, workflow_name);
        PredefinedWorkflowDescribeInterfaceRequest data = new PredefinedWorkflowDescribeInterfaceRequest();
        data.setApiKey(roboflowConfig.getApiKey());
        data.setUseCache(false);
        return WORKFLOW_DESCRIBE_INTERFACE_PREDEFINED.send(requestSenderOfOKHttp, data, roboflowConfig, buildWorkflowPathMap(workspace_name, workflow_name));
    }

    /**
     * Synchronously sends a Base64-encoded image to Roboflow and retrieves predictions.
     * <br> This method sends the image to the Roboflow API, retrieves the predictions, processes them, and returns a list of
     * predictions with mapped class labels.
     *
     * @param base64String The Base64-encoded image string to be sent to Roboflow.
     * @return A list of {@link SinglePrediction} objects representing the predictions from Roboflow.
     */
    public List<SinglePrediction> sendImg(String base64String, String workspace_name, String workflow_name, String workflow_id) {
        try {

            RoboflowResponseData send = runPredefineWorkflowOnImage(requestSenderOfOKHttp, roboflowConfig, workspace_name, workflow_name, base64String, workflow_id);
            if (send instanceof HTTPValidationError) {
                log.error("HTTPValidationError when sync sending img:: {}", send);
            }
            if (send instanceof WorkflowInferenceResponseDTO response) {
                WorkflowOutputData workflowOutputData = response.toEntity().getOutputs().get(0);
                workflowOutputData.getOutputImage().saveToDir("D:\\桌面文件\\serverSrc\\proceed_images\\");

                List<SinglePrediction> predictions = workflowOutputData
                        .getPredictions()
                        .getPredictions();
                predictions = predictions.stream().peek(singlePrediction -> {
                    String clazz = singlePrediction.getClazz();
                    clazz = clazz.toLowerCase();
                    singlePrediction.setLabel(clazz);
                    if(clazz.startsWith("object")) {
                        String object1 = clazz.replace("object", "");
                        if(object1.startsWith(" ")) object1 = object1.substring(1);
                        Optional<ObjectWithId> object = objectRepository.findById(Integer.parseInt(object1));
                        object.ifPresent(objectWithId -> singlePrediction.setClazz(objectWithId.getName()));
                    } else if(clazz.startsWith("action")) {
                        String object1 = clazz.replace("action", "");
                        if(object1.startsWith(" ")) object1 = object1.substring(1);
                        Optional<ActionWithId> object = actionRepository.findById(Integer.parseInt(object1));
                        object.ifPresent(objectWithId -> singlePrediction.setClazz(objectWithId.getName()));
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
     * Asynchronously sends a Base64-encoded image to Roboflow and retrieves predictions.
     * <br> This method initiates the image processing asynchronously, allowing non-blocking operations and returns a
     * {@link CompletableFuture} that will eventually contain a list of predictions.
     *
     * @param base64String The Base64-encoded image string to be sent to Roboflow.
     * @return A {@link CompletableFuture} containing a list of {@link AbstractActionObservation} objects representing the predictions.
     */
    public CompletableFuture<List<AbstractActionObservation>> sendImgAsync(String base64String, String workspace_name, String workflow_name, String workflow_id) {

        CompletableFuture<RoboflowResponseData> completableFuture = null;
        try {
            completableFuture = runPredefineWorkflowOnImageAsync(requestSenderOfOKHttp, roboflowConfig, workspace_name, workflow_name, base64String, workflow_id);
        } catch (IOException | ExecutionException | InterruptedException e) {
            log.error(e.getMessage());
            CompletableFuture<List<AbstractActionObservation>> completableFutureEx = new CompletableFuture<>();
            completableFutureEx.complete(new ArrayList<>());
            return completableFutureEx;
        }

        return completableFuture.thenApply(send -> {
            if (send instanceof HTTPValidationError) {
                log.error("HTTPValidationError when async sending img: {}", send);
            }
            if (send instanceof WorkflowInferenceResponseDTO response) {
                if (response.toEntity().getOutputs().size() != 1) {
                    log.error("Error: Too many outputs: {}", response.toEntity().getOutputs());
                }
                WorkflowOutputData workflowOutputData = response.toEntity().getOutputs().get(0);
                workflowOutputData.getOutputImage().saveToDir("D:\\桌面文件\\serverSrc\\proceed_images\\");

                return workflowOutputData.getPredictions()
                        .getPredictions().stream().map(singlePrediction -> {
                            String clazz = singlePrediction.getClazz();
                            clazz = clazz.toLowerCase();
                            singlePrediction.setLabel(clazz);
                            if(clazz.startsWith("object")) {
                                String object1 = clazz.replace("object", "");
                                if(object1.startsWith(" ")) object1 = object1.substring(1);
                                Optional<ObjectWithId> object = objectRepository.findById(Integer.parseInt(object1));
                                object.ifPresent(objectWithId -> singlePrediction.setClazz(objectWithId.getName()));
                            } else if(clazz.startsWith("action")) {
                                String object1 = clazz.replace("action", "");
                                if(object1.startsWith(" ")) object1 = object1.substring(1);
                                Optional<ActionWithId> object = actionRepository.findById(Integer.parseInt(object1));
                                object.ifPresent(objectWithId -> singlePrediction.setClazz(objectWithId.getName()));
                            }
                            return (AbstractActionObservation) singlePrediction;
                        }).toList();
            }
            return new ArrayList<>();
        });
    }
}
