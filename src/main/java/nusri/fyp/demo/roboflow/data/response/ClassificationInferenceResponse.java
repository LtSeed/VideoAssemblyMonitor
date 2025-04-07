package nusri.fyp.demo.roboflow.data.response;

import lombok.Data;
import nusri.fyp.demo.roboflow.data.entity.ClassificationPrediction;
import nusri.fyp.demo.roboflow.data.RoboflowResponseData;
import nusri.fyp.demo.roboflow.data.entity.InferenceImageDimensions;

import java.util.List;

/**
 * Represents the response for a classification inference request to the Roboflow API.
 * <br> This class contains the data returned by the API after performing classification inference on an image.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@Data
public class ClassificationInferenceResponse implements RoboflowResponseData {

    /**
     * The URL or path to the visualization of the classification results.
     */
    private String visualization;

    /**
     * The unique identifier for the inference request.
     */
    private String inferenceId;

    /**
     * The frame ID of the video or sequence in which the inference was made, if applicable.
     */
    private Integer frameId;

    /**
     * The time taken to perform the inference, typically in seconds.
     */
    private Double time;

    /**
     * The dimensions of the image on which the inference was performed.
     */
    private InferenceImageDimensions image;

    /**
     * A list of classification predictions made by the model.
     */
    private List<ClassificationPrediction> predictions;

    /**
     * The top classification label.
     */
    private String top;

    /**
     * The confidence score associated with the top classification.
     */
    private Float confidence;

    /**
     * The parent ID associated with the inference, if applicable.
     */
    private String parentId;
}
