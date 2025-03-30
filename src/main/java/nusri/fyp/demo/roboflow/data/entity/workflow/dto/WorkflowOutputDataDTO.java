package nusri.fyp.demo.roboflow.data.entity.workflow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import nusri.fyp.demo.roboflow.data.entity.workflow.OutputImage;
import nusri.fyp.demo.roboflow.data.entity.workflow.WorkflowOutputData;

/**
 * DTO for output data from the workflow, including predictions, object count, and output image.
 */
@Data
public class WorkflowOutputDataDTO {
    /**
     * Predictions related to the workflow output.
     */
    private PredictionsDTO predictions;

    /**
     * The count of objects detected in the workflow output.
     */
    @JsonProperty("count_objects")
    private int countObjects;

    /**
     * The output image from the workflow.
     */
    @JsonProperty("output_image")
    private OutputImage outputImage;

    /**
     * Converts this DTO to an entity {@link WorkflowOutputData}.
     *
     * @return The converted entity object.
     */
    public WorkflowOutputData toEntity(){
        WorkflowOutputData response = new WorkflowOutputData();
        response.setPredictions(predictions.toEntity());
        response.setCountObjects(countObjects);
        response.setOutputImage(outputImage);
        return response;
    }
}