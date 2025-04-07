package nusri.fyp.demo.roboflow.data.response;

import lombok.Data;
import nusri.fyp.demo.roboflow.data.RoboflowResponseData;

/**
 * Represents the response for OCR (Optical Character Recognition) inference in the Roboflow API.
 * <br> This class contains the data returned by the API after performing OCR inference on an image.
 * <br> The response includes the OCR result, the time taken for the inference, and an optional parent ID.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@Data
public class OCRInferenceResponse implements RoboflowResponseData {

    /**
     * The OCR result, which is the text extracted from the image.
     * <br> This is the primary output of the OCR inference.
     */
    private String result;

    /**
     * The time taken to perform the OCR inference, typically in seconds.
     */
    private Double time;

    /**
     * The parent ID associated with the OCR inference, if applicable.
     * <br> This could represent a relationship to a larger processing task or data group.
     */
    private String parentId;
}
