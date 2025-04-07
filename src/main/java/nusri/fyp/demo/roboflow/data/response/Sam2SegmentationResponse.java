package nusri.fyp.demo.roboflow.data.response;

import lombok.Data;
import nusri.fyp.demo.roboflow.data.RoboflowResponseData;
import nusri.fyp.demo.roboflow.data.entity.Sam2SegmentationPrediction;

import java.util.List;

/**
 * Represents the response for SAM2 (Segmentation and Masking) segmentation inference in the Roboflow API.
 * <br> This class contains the data returned by the API after performing SAM2 segmentation on an image.
 * <br> The response includes a list of segmentation predictions and the time taken for the inference.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@Data
public class Sam2SegmentationResponse implements RoboflowResponseData {

    /**
     * A list of {@link Sam2SegmentationPrediction} objects representing the segmentation predictions made by the model.
     * <br> Each prediction corresponds to a segmented area in the image.
     */
    private List<Sam2SegmentationPrediction> predictions;

    /**
     * The time taken to perform the SAM2 segmentation, typically in seconds.
     * <br> This field indicates the duration of the segmentation process.
     */
    private double time;
}
