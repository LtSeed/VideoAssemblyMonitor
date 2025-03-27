package nusri.fyp.demo.roboflow.data.response;

import lombok.Data;
import nusri.fyp.demo.roboflow.data.RoboflowResponseData;

import java.util.List;

/**
 * Represents the response for SAM (Segmentation and Masking) segmentation in the Roboflow API.
 * <br> This class contains the data returned by the API after performing SAM segmentation on an image.
 * <br> The response includes the segmentation masks and the time taken for the segmentation process.
 */
@Data
public class SamSegmentationResponse implements RoboflowResponseData {

    /**
     * A nested list representing the segmentation masks for the image.
     * <br> Each mask corresponds to a segmented area in the image. The values in the mask are typically binary or categorical.
     * <br> The type can be changed to Double if more precision is required instead of Integer.
     */
    private List<List<List<Integer>>> masks;

    /**
     * A nested list representing the low-resolution segmentation masks for the image.
     * <br> These masks are similar to the main masks but at a lower resolution, providing a less detailed segmentation.
     * <br> The type can be changed to Double if more precision is required instead of Integer.
     */
    private List<List<List<Integer>>> lowResMasks;

    /**
     * The time taken to perform the SAM segmentation, typically in seconds.
     * <br> This field indicates the duration of the segmentation process.
     */
    private double time;
}
