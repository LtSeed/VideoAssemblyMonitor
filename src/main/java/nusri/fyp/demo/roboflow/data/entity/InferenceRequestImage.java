package nusri.fyp.demo.roboflow.data.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nusri.fyp.demo.roboflow.data.AnyData;
import nusri.fyp.demo.roboflow.data.RoboflowRequestData;

/**
 * Represents image data for an inference request.
 * <br> This class encapsulates various image data formats and properties used in making inference requests.
 * <br> It supports multiple input types such as URL, base64 encoded strings, or numpy arrays.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InferenceRequestImage extends RoboflowRequestData {

    /**
     * The type of image data provided.
     * <ul>
     *   <li>"url" - the image is provided as a URL.</li>
     *   <li>"base64" - the image is provided as a base64 encoded string.</li>
     *   <li>"numpy" - the image is provided as a numpy array.</li>
     * </ul>
     */
    private String type;

    /**
     * The image value.
     * <br> For URLs, this will be the URL of the image.
     * For base64, this will be the base64 encoded string.
     * For numpy arrays, it could represent a serialized array.
     */
    private String value;

    /**
     * The file path of the image.
     * <br> This could be used when referencing the image location from the file system.
     */
    private String path;

    /**
     * Prefix to be used with the image URL or file path.
     * <br> This could be useful for appending a prefix to the image path or URL when required.
     */
    private String prefix;

    /**
     * The new dimensions of the image after potential resizing.
     * <br> This represents the width and height after resizing, if applicable.
     */
    private InferenceImageDimensions newDimensions;

    /**
     * The original dimensions of the image.
     * <br> This represents the original width and height of the image before any transformations.
     */
    private InferenceImageDimensions originalDimensions;

    /**
     * Indicates whether the image has been resized.
     * <br> This is a boolean flag that shows if the image's dimensions have been altered from the original.
     */
    private Boolean resized;

    /**
     * Returns a string representation of the image data.
     * <br> It includes the type of image and the size of the value data.
     *
     * @return A string describing the image type and the size of the value.
     */
    @Override
    public String toString() {
        return "InferenceRequestImage [type=" + type + ", valueSize=" + value.length() + "]";
    }
}
