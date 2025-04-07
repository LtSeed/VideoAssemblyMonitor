package nusri.fyp.demo.roboflow.data.response;

import lombok.Data;
import nusri.fyp.demo.roboflow.data.RoboflowResponseData;

/**
 * Represents a stub response in the Roboflow API.
 * <br> This class contains data for a mock or placeholder response, typically used in scenarios where a response is required but no actual inference is performed.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@Data
public class StubResponse implements RoboflowResponseData {

    /**
     * A URL or path to the visualization of the stub response.
     * <br> This can be a placeholder visualization or mock result.
     */
    private String visualization;

    /**
     * The unique identifier for the inference request.
     * <br> This is typically used to reference the inference task.
     */
    private String inferenceId;

    /**
     * The frame ID of the video or sequence in which the inference was performed, if applicable.
     * <br> This is used to track the specific frame of a video for which the stub response is generated.
     */
    private Integer frameId;

    /**
     * The time taken to process the stub response, typically in seconds.
     * <br> This field indicates the duration of the stub process, which may be zero or a mock value.
     */
    private double time;

    /**
     * A boolean indicating whether the response is a stub or actual result.
     * <br> This is typically set to true for stub responses.
     */
    private boolean isStub;

    /**
     * The identifier of the model associated with the stub response.
     * <br> This can be used to reference the model that generated the mock result.
     */
    private String modelId;

    /**
     * The type of task associated with the stub response, such as "object_detection", "segmentation", etc.
     * <br> This helps to identify the type of task for which the stub response is generated.
     */
    private String taskType;
}
