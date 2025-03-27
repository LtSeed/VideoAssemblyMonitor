package nusri.fyp.demo.roboflow.data.entity;

import lombok.Data;
import java.util.List;

/**
 * Represents a set of SAM (Segment Anything Model) prompts.
 * <br> This class contains a list of `Sam2Prompt` objects, each representing a single prompt for SAM-based segmentation.
 * <br> The set of prompts can be used to guide the model to segment multiple regions or provide multiple point-based inputs for segmentation.
 */
@Data
public class Sam2PromptSet {

    /**
     * A list of SAM prompts.
     * <br> Each prompt includes a bounding box and a set of points that guide the model in its segmentation task.
     */
    private List<Sam2Prompt> prompts;
}
