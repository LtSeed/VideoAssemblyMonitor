package nusri.fyp.demo.roboflow.data.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nusri.fyp.demo.roboflow.data.RoboflowRequestData;
import nusri.fyp.demo.roboflow.data.entity.InferenceRequestImage;

import java.util.List;

/**
 * Represents a request for DocTR OCR inference in the Roboflow API.
 * <br> This class is used to send a request to the Roboflow API to perform OCR (Optical Character Recognition) inference using the DocTR model.
 * The request processes images to extract textual information.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DoctrOCRInferenceRequest extends RoboflowRequestData {

    /**
     * The unique identifier for the inference request.
     * <br> This field is used to reference and track the specific OCR inference request.
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
     * <br> This field indicates when the DocTR OCR inference started.
     */
    private Double start;

    /**
     * The source of the image being processed.
     * <br> This can describe where the image originates from (e.g., "web", "camera", etc.).
     */
    private String source;

    /**
     * Additional information about the image source.
     * <br> This can provide context or metadata about the image source.
     */
    private String sourceInfo;

    /**
     * The image to be processed for OCR.
     * <br> This field can be an array of objects or a single object representing the image data.
     * The image will be processed to extract textual information.
     */
    private InferenceRequestImage image;

    /**
     * The version ID of the DocTR model being used for OCR inference.
     * <br> This field specifies which version of the DocTR model should be used.
     */
    private String doctrVersionId;

    /**
     * The model ID used for the OCR inference process.
     * <br> This field specifies which model will process the image for OCR.
     */
    private String modelId;
}
