package nusri.fyp.demo.roboflow.data.entity.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Represents the output data of a workflow inference.
 * This class encapsulates the results of a workflow execution, including the count of detected objects,
 * the output image, and predictions made by the workflow.
 * <br>
 * Example of the expected output:
 * <pre>
 * {
 *   "count_objects": 10,
 *   "output_image": { ... },
 *   "predictions": {
 *     "image": { "width": null, "height": null },
 *     "predictions": [...]
 *   }
 * }
 * </pre>
 */
@Data
public class WorkflowOutputData {

    /**
     * The count of detected objects in the workflow result.
     */
    @JsonProperty("count_objects")
    private int countObjects;

    /**
     * The output image of the workflow. This may include visualized results such as bounding boxes, labels, etc.
     */
    @JsonProperty("output_image")
    private OutputImage outputImage;

    /**
     * The predictions made by the workflow, including the associated image and detailed prediction results.
     * The predictions may include various data points such as object locations, labels, and other relevant details.
     */
    private Predictions predictions;
}
