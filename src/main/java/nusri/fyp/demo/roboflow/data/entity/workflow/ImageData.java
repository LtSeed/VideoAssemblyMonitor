package nusri.fyp.demo.roboflow.data.entity.workflow;

import lombok.Data;

/**
 * Represents an image with width and height properties.
 * <br> This class holds the dimensions of an image, which may be used for processing, transformation, or analysis.
 * <br> The width and height are optional and can be null.
 */
@Data
public class ImageData {

    /**
     * The width of the image.
     * <br> This value can be null if the image width is not specified.
     */
    private Integer width;

    /**
     * The height of the image.
     * <br> This value can be null if the image height is not specified.
     */
    private Integer height;
}
