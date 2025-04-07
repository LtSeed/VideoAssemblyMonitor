package nusri.fyp.demo.roboflow.data.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nusri.fyp.demo.roboflow.data.AnyData;
import nusri.fyp.demo.roboflow.data.RoboflowRequestData;

/**
 * Represents a request for CLIP text embedding in the Roboflow API.
 * <br> This class is used to send a request to the Roboflow API to perform CLIP text embedding,
 * which generates a feature vector representation of text using the CLIP model.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ClipTextEmbeddingRequest extends RoboflowRequestData {

    /**
     * The unique identifier for the inference request.
     * <br> This field is used to reference and track the specific embedding request.
     */
    private String id;

    /**
     * The API key associated with the request.
     * <br> This is required for authentication and to associate the request with a specific account.
     */
    private String apiKey;

    /**
     * A flag indicating whether the inference usage is billable.
     * <br> This field specifies whether the request will incur charges.
     */
    private Boolean usageBillable;

    /**
     * The start time of the inference request (in seconds).
     * <br> This field indicates when the CLIP text embedding started.
     */
    private Double start;

    /**
     * The source of the text being processed.
     * <br> This can describe where the text originates from (e.g., "web", "user input", etc.).
     */
    private String source;

    /**
     * Additional information about the text source.
     * <br> This can provide context or metadata about the text source.
     */
    private String sourceInfo;

    /**
     * The CLIP version ID being used for the text embedding.
     * <br> This field specifies which version of the CLIP model should be used.
     */
    private String clipVersionId;

    /**
     * The model ID used for the embedding process.
     * <br> This field specifies which model will process the text embedding request.
     */
    private String modelId;

    /**
     * The text to be embedded.
     * <br> This field can be a single string or an array of strings representing the text data.
     * The text will be processed to generate its feature vector representation.
     */
    private AnyData text;
}
