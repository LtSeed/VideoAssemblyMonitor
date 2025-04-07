package nusri.fyp.demo.roboflow.data.entity;

import lombok.Data;

/**
 * Represents the dimensions of an image in an inference response.
 * <br> This class holds the width and height of an image, typically used to describe the resolution of an image
 * in the context of inference results.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@Data
public class InferenceImageDimensions {

    /**
     * The width of the image.
     * <br> This field represents the horizontal dimension of the image in pixels.
     */
    private Integer width;

    /**
     * The height of the image.
     * <br> This field represents the vertical dimension of the image in pixels.
     */
    private Integer height;

    /**
     * Constructs an instance of {@link InferenceImageDimensions} with the specified width and height.
     *
     * @param width  The width of the image in pixels.
     * @param height The height of the image in pixels.
     */
    public InferenceImageDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
