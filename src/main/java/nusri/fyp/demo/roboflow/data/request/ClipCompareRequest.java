package nusri.fyp.demo.roboflow.data.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nusri.fyp.demo.roboflow.data.AnyData;
import nusri.fyp.demo.roboflow.data.RoboflowRequestData;

/**
 * Represents a request for CLIP comparison in the Roboflow API.
 * <br> This class is used to send a request to the Roboflow API to perform CLIP (Contrastive Language-Image Pretraining) comparison,
 * which compares an image with a prompt to find the most similar matches.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ClipCompareRequest extends RoboflowRequestData {

    /**
     * The unique identifier for the inference request.
     * <br> This field is used to reference and track the specific comparison request.
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
     * <br> This field indicates when the CLIP comparison started.
     */
    private Double start;

    /**
     * The source of the image or data being compared.
     * <br> This can describe where the image or data originates (e.g., "web", "camera", etc.).
     */
    private String source;

    /**
     * Additional information about the image source.
     * <br> This can provide context or metadata about the image source.
     */
    private String sourceInfo;

    /**
     * The CLIP version ID being used for the comparison.
     * <br> This field specifies which version of the CLIP model should be used.
     */
    private String clipVersionId;

    /**
     * The model ID used for the comparison.
     * <br> This field specifies which model will process the comparison.
     */
    private String modelId;

    /**
     * The subject of the comparison.
     * <br> This can be an image or text (Object or String), representing the subject being compared.
     */
    private AnyData subject;

    /**
     * The type of the subject (e.g., "image", "text").
     * <br> This field defines what type of data is being compared (image, text, etc.).
     */
    private String subjectType;

    /**
     * The prompt used for the comparison.
     * <br> This can be an image, text, or a combination of both (Array<Object>, Object, String, Array<String>, or Map<String, Object>).
     */
    private AnyData prompt;

    /**
     * The type of the prompt (e.g., "image", "text").
     * <br> This field defines the type of the prompt used in the CLIP comparison.
     */
    private String promptType;
}
