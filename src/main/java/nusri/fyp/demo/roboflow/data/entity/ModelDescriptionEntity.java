package nusri.fyp.demo.roboflow.data.entity;

import lombok.Data;

/**
 * Entity for describing a model.
 * <br> This class encapsulates the key details about a model, such as its unique identifier, task type,
 * batch size, and input dimensions (height and width) that are required for model inference.
 */
@Data
public class ModelDescriptionEntity {

    /**
     * The unique identifier for the model.
     * <br> This identifier is used to reference the specific model within a system or workflow.
     */
    private String modelId;

    /**
     * The task type the model is designed for.
     * <br> This could include types such as object detection, image classification, etc.
     */
    private String taskType;

    /**
     * The batch size used by the model during inference.
     * <br> This defines how many samples the model processes in a single batch.
     */
    private String batchSize;

    /**
     * The input height required by the model.
     * <br> This represents the expected height of the input images (in pixels) for the model.
     */
    private Integer inputHeight;

    /**
     * The input width required by the model.
     * <br> This represents the expected width of the input images (in pixels) for the model.
     */
    private Integer inputWidth;
}
