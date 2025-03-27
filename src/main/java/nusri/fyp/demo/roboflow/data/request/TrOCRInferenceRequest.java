package nusri.fyp.demo.roboflow.data.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nusri.fyp.demo.roboflow.data.RoboflowRequestData;
import nusri.fyp.demo.roboflow.data.entity.InferenceRequestImage;

/**
 * Represents a request for TrOCR (Transformer OCR) inference.
 * <br> This request is used to perform optical character recognition (OCR) on an image using the TrOCR model.
 * The request includes parameters such as the image data, model configurations, and other relevant settings for performing OCR.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TrOCRInferenceRequest extends RoboflowRequestData {

    /**
     * The unique identifier for the inference request.
     * <br> This field is used to track the request and associate it with the result.
     */
    private String id;

    /**
     * The API key required to authenticate the request.
     * <br> This field is necessary to verify the user's access to the OCR service.
     */
    private String apiKey;

    /**
     * A flag indicating whether the usage of this request should be billed.
     * <br> If true, the request will be billed as per the usage plan.
     */
    private boolean usageBillable;

    /**
     * The start time for the OCR process.
     * <br> This field is typically used to track or log the timing of the request.
     */
    private Double start;

    /**
     * The source identifier for the image being processed.
     * <br> This field references the source of the image, such as a URL or local path.
     */
    private String source;

    /**
     * Additional information about the source of the image.
     * <br> This field can be used for any extra details related to the image source.
     */
    private String sourceInfo;

    /**
     * The image to be processed by the TrOCR model for optical character recognition.
     * <br> This field contains the image data that will be processed for text extraction.
     */
    private InferenceRequestImage image;

    /**
     * The version ID of the TrOCR model being used for the inference.
     * <br> This specifies which version of the TrOCR model should be applied to the image.
     */
    private String trocrVersionId;

    /**
     * The model ID to be used for performing OCR.
     * <br> This field refers to the specific model in the TrOCR system that will be used for the inference.
     */
    private String modelId;
}
