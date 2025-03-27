package nusri.fyp.demo.roboflow.data.entity;

import lombok.Data;
import java.util.List;

/**
 * Represents a SAM (Segment Anything Model) segmentation prediction.
 * <br> This class contains the segmentation results produced by SAM, including the generated masks and their corresponding confidence.
 * <br> The masks are represented as multi-dimensional lists where each mask corresponds to a segmented region in the image.
 */
@Data
public class Sam2SegmentationPrediction {

    /**
     * A list of high-resolution segmentation masks.
     * <br> Each mask is represented as a 2D array of integers where the mask pixels are denoted by integer values.
     * These masks are the final outputs of the segmentation task.
     */
    private List<List<List<Integer>>> masks;

    /**
     * A list of low-resolution segmentation masks.
     * <br> Each mask is represented as a 2D array of integers, similar to the high-resolution masks but at a lower resolution.
     * These are typically used to quickly compute rough segmentation results.
     */
    private List<List<List<Integer>>> lowResMasks;

    /**
     * The time taken for the segmentation process in seconds.
     * <br> This field records how long it took for the model to generate the segmentation results.
     */
    private double time;

    /**
     * The confidence score of the segmentation.
     * <br> A value between 0 and 1 indicating the confidence level of the segmentation results.
     */
    private double confidence;
}
