package nusri.fyp.demo.roboflow.data.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nusri.fyp.demo.roboflow.data.RoboflowRequestData;
import nusri.fyp.demo.roboflow.data.entity.InferenceRequestImage;

import java.util.List;

/**
 * Represents a request for YOLO world inference.
 * <br> This request triggers the inference process using the YOLO (You Only Look Once) model for object detection
 * and classification on a provided image with optional text inputs.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class YOLOWorldInferenceRequest extends RoboflowRequestData {

    /**
     * The ID of the request.
     * <br> This field is used to uniquely identify the request.
     */
    private String id;

    /**
     * The API key required to authenticate the request.
     * <br> This field is used to authenticate the request and gain access to the YOLO world inference functionality.
     */
    private String apiKey;

    /**
     * Indicates whether the usage of the API will be billable.
     * <br> This field specifies if the inference request will be billed for usage.
     */
    private Boolean usageBillable;

    /**
     * The start time of the request in Unix timestamp format.
     * <br> This field is used to track when the inference request is initiated.
     */
    private Double start;

    /**
     * The source of the image data.
     * <br> This field provides information about the source of the image to be processed.
     */
    private String source;

    /**
     * Additional information about the source of the image.
     * <br> This field can contain extra details about the image source for context.
     */
    private String sourceInfo;

    /**
     * The ID of the model to be used for the inference.
     * <br> This field specifies which model is to be used for object detection and classification.
     */
    private String modelId;

    /**
     * The type of the model used for inference.
     * <br> This field specifies the type or version of the YOLO model to be used.
     */
    private String modelType;

    /**
     * The image to be used for inference.
     * <br> This field contains the image data that will be processed by the YOLO model. It can be an array or a single object.
     */
    private InferenceRequestImage image;

    /**
     * The text associated with the image for inference, used for detection tasks.
     * <br> This field can contain text-based data, like captions or labels, that may assist in the inference task.
     */
    private List<String> text;

    /**
     * The version ID of the YOLO world model.
     * <br> This field specifies the version of the YOLO model to be used for the inference process.
     */
    private String yoloWorldVersionId;

    /**
     * The confidence threshold for the inference.
     * <br> This field sets the minimum confidence level required for the model to consider an object detection as valid.
     */
    private Float confidence;
}
