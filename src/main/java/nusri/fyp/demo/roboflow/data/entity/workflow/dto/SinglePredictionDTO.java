package nusri.fyp.demo.roboflow.data.entity.workflow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import nusri.fyp.demo.roboflow.data.entity.workflow.SinglePrediction;

/**
 * Corresponds to a single prediction from the third-party JSON response.
 */
@Data
public class SinglePredictionDTO {

    /**
     * The label or class of the prediction.
     */
    private String label;

    /**
     * The confidence level of the prediction.
     */
    private Double confidence;

    /**
     * The x-coordinate of the top-left corner of the bounding box.
     */
    private int x;

    /**
     * The y-coordinate of the top-left corner of the bounding box.
     */
    private int y;

    /**
     * The width of the bounding box.
     */
    private int width;

    /**
     * The height of the bounding box.
     */
    private int height;

    /**
     * The class ID associated with the prediction.
     */
    @JsonProperty("class_id")
    private int classId;

    /**
     * The class of the prediction.
     */
    @JsonProperty("class")
    private String clazz;

    /**
     * The detection ID associated with the prediction.
     */
    @JsonProperty("detection_id")
    private String detectionId;

    /**
     * The parent ID associated with the prediction.
     */
    @JsonProperty("parent_id")
    private String parentId;

    /**
     * Converts this DTO to an entity {@link nusri.fyp.demo.roboflow.data.entity.workflow.SinglePrediction}.
     *
     * @return The converted entity object.
     */
    public SinglePrediction toEntity() {
        SinglePrediction singlePrediction = new SinglePrediction();
        singlePrediction.setLabel(label);
        singlePrediction.setConfidence(confidence);
        singlePrediction.setX(x);
        singlePrediction.setY(y);
        singlePrediction.setWidth(width);
        singlePrediction.setHeight(height);
        singlePrediction.setClassId(classId);
        singlePrediction.setClazz(clazz);
        singlePrediction.setDetectionId(detectionId);
        singlePrediction.setParentId(parentId);
        return singlePrediction;
    }

    // Note: No @JsonTypeInfo annotation! This is a standard DTO.
}