package nusri.fyp.demo.roboflow.data.response;

import lombok.Data;
import nusri.fyp.demo.roboflow.data.AnyData;
import nusri.fyp.demo.roboflow.data.RoboflowResponseData;

/**
 * Represents the response for a CLIP comparison request to the Roboflow API.
 * <br> This class contains the data returned by the API after performing a CLIP (Contrastive Language-Image Pretraining) comparison.
 */
@Data
public class ClipCompareResponse implements RoboflowResponseData {

    /**
     * The unique identifier for the CLIP comparison inference request.
     */
    private String inferenceId;

    /**
     * The frame ID of the video or sequence in which the comparison was made, if applicable.
     */
    private Integer frameId;

    /**
     * The time taken to perform the CLIP comparison, typically in seconds.
     */
    private Double time;

    /**
     * The similarity score or scores resulting from the CLIP comparison.
     * <br> This can be a list of floats or a map of string keys to float values.
     */
    private AnyData similarity;

    /**
     * The parent ID associated with the CLIP comparison, if applicable.
     */
    private String parentId;
}
