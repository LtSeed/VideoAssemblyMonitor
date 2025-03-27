package nusri.fyp.demo.roboflow.data.entity;

import lombok.Data;
import java.util.List;

/**
 * Represents a SAM (Segment Anything Model) prompt.
 * <br> This class is used to define a prompt for SAM-based segmentation models, which include a bounding box and associated points.
 */
@Data
public class Sam2Prompt {

    /**
     * The bounding box for the prompt.
     * <br> This defines the rectangular region in the image that the model should focus on for segmentation.
     */
    private Box box;

    /**
     * The points associated with the prompt.
     * <br> These are additional points that can guide the SAM model in performing more accurate segmentation.
     */
    private List<Point> points;
}
