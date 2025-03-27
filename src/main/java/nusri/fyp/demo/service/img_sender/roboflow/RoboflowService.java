package nusri.fyp.demo.service.img_sender.roboflow;

import lombok.extern.slf4j.Slf4j;
import nusri.fyp.demo.dto.WorkflowInferenceResponseDTO;
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
import nusri.fyp.demo.roboflow.data.request.WorkflowSpecificationInferenceRequest;
import nusri.fyp.demo.roboflow.data.response.*;
import nusri.fyp.demo.roboflow.request.RequestSenderOfOKHttp;
import nusri.fyp.demo.roboflow.request.RoboflowRequest;
import nusri.fyp.demo.state_machine.AbstractActionObservation;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
     */
    RoboflowService(RequestSenderOfOKHttp requestSenderOfOKHttp,
                    ObjectRepository objectRepository,
                    ActionRepository actionRepository,
                    RoboflowConfig roboflowConfig) {
        this.requestSenderOfOKHttp = requestSenderOfOKHttp;
        this.objectRepository = objectRepository;
        this.actionRepository = actionRepository;
        this.roboflowConfig = roboflowConfig;
    }

    /**
     * Synchronously sends a Base64-encoded image to Roboflow and retrieves predictions.
     * <br> This method sends the image to the Roboflow API, retrieves the predictions, processes them, and returns a list of
     * predictions with mapped class labels.
     *
     * @param base64String The Base64-encoded image string to be sent to Roboflow.
     * @return A list of {@link SinglePrediction} objects representing the predictions from Roboflow.
     */
    public List<SinglePrediction> sendImg(String base64String) {
        try {
            WorkflowSpecificationInferenceRequest request = getRequest(base64String);
            RoboflowResponseData send = RoboflowRequest.WORKFLOW_RUN_SPECIFICATION.send(requestSenderOfOKHttp, request, roboflowConfig);
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

        } catch (ExecutionException | InterruptedException e) {
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
    public CompletableFuture<List<AbstractActionObservation>> sendImgAsync(String base64String) {
        WorkflowSpecificationInferenceRequest request = getRequest(base64String);
        CompletableFuture<RoboflowResponseData> completableFuture = RoboflowRequest.WORKFLOW_RUN_SPECIFICATION.sendAsync(requestSenderOfOKHttp, request, roboflowConfig);

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

    /**
     * Constructs a {@link WorkflowSpecificationInferenceRequest} containing the required parameters for the inference request.
     * <br> This method creates a new request to send to the Roboflow API, including the Base64-encoded image and other necessary fields.
     *
     * @param base64String The Base64-encoded image string to be sent to Roboflow.
     * @return A {@link WorkflowSpecificationInferenceRequest} containing all necessary fields for the inference request.
     */
    private WorkflowSpecificationInferenceRequest getRequest(String base64String) {
        WorkflowSpecificationInferenceRequest request = new WorkflowSpecificationInferenceRequest();
        request.setApiKey(roboflowConfig.getApiKey());
        InferenceRequestImage image = new InferenceRequestImage();
        image.setType("base64");
        image.setValue(base64String);
        image.setPath("test2.png");
        image.setPrefix("data:image/png;base64,");
        image.setNewDimensions(new InferenceImageDimensions(2048, 1114));
        image.setOriginalDimensions(new InferenceImageDimensions(2560, 1392));
        image.setResized(false);

        request.addImage(image);
        request.setIsPreview(true);
        request.setWorkflowId("NUqv8lH6pNS8yfNxmfmQ");
        request.setSpecification(buildSpecification());
        return request;
    }

    /**
     * Builds the specification for the inference workflow that is sent to Roboflow.
     * <br> This method constructs the entire inference workflow, including steps for object detection, visualization, and counting.
     *
     * @return A Map representing the inference workflow specification, which will be serialized into JSON and sent to Roboflow.
     */
    public static Map<String, Object> buildSpecification() {
        Map<String, Object> specification = new HashMap<>();
        specification.put("version", "1.0");

        List<Map<String, Object>> inputs = new ArrayList<>();
        Map<String, Object> input = new HashMap<>();
        input.put("type", "InferenceImage");
        input.put("name", "image");
        inputs.add(input);
        specification.put("inputs", inputs);

        List<Map<String, Object>> steps = getStep();
        specification.put("steps", steps);

        List<Map<String, Object>> outputs = getOutput();
        specification.put("outputs", outputs);

        return specification;
    }

    private static List<Map<String, Object>> getOutput() {
        List<Map<String, Object>> outputs = new ArrayList<>();

        Map<String, Object> output1 = new HashMap<>();
        output1.put("type", "JsonField");
        output1.put("name", "count_objects");
        output1.put("coordinates_system", "own");
        output1.put("selector", "$steps.count_objects.output");
        outputs.add(output1);

        Map<String, Object> output2 = new HashMap<>();
        output2.put("type", "JsonField");
        output2.put("name", "output_image");
        output2.put("coordinates_system", "own");
        output2.put("selector", "$steps.annotated_image.image");
        outputs.add(output2);

        Map<String, Object> output3 = new HashMap<>();
        output3.put("type", "JsonField");
        output3.put("name", "predictions");
        output3.put("coordinates_system", "own");
        output3.put("selector", "$steps.model.predictions");
        outputs.add(output3);

        return outputs;
    }

    private static List<Map<String, Object>> getStep() {
        List<Map<String, Object>> steps = new ArrayList<>();

        Map<String, Object> step1 = new HashMap<>();
        step1.put("type", "roboflow_core/roboflow_object_detection_model@v1");
        step1.put("name", "model");
        step1.put("images", "$inputs.image");
        step1.put("model_id", "small-rice-cooker/7");
        steps.add(step1);

        Map<String, Object> step2 = new HashMap<>();
        step2.put("type", "roboflow_core/bounding_box_visualization@v1");
        step2.put("name", "detection_visualization");
        step2.put("image", "$inputs.image");
        step2.put("predictions", "$steps.model.predictions");
        steps.add(step2);

        Map<String, Object> step3 = new HashMap<>();
        step3.put("type", "roboflow_core/property_definition@v1");
        step3.put("name", "count_objects");
        step3.put("data", "$steps.model.predictions");

        List<Map<String, Object>> operations = new ArrayList<>();
        Map<String, Object> op = new HashMap<>();
        op.put("type", "SequenceLength");
        operations.add(op);
        step3.put("operations", operations);
        steps.add(step3);

        Map<String, Object> step4 = new HashMap<>();
        step4.put("type", "roboflow_core/label_visualization@v1");
        step4.put("name", "annotated_image");
        step4.put("image", "$steps.detection_visualization.image");
        step4.put("predictions", "$steps.model.predictions");
        steps.add(step4);

        return steps;
    }
}
