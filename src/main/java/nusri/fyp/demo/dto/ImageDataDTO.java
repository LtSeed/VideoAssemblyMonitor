package nusri.fyp.demo.dto;

import lombok.Data;
import nusri.fyp.demo.roboflow.data.entity.workflow.ImageData;

/**
 * DTO class for receiving or transferring the width and height information of an image.
 * <br>
 * Provides a method to convert it to an entity {@link nusri.fyp.demo.roboflow.data.entity.workflow.ImageData}.
 */
@Data
public class ImageDataDTO {
    /**
     * The width of the image (in pixels), can be null.
     */
    private Integer width;   // Allows null

    /**
     * The height of the image (in pixels), can be null.
     */
    private Integer height;  // Allows null

    /**
     * Converts the current DTO to an entity {@link nusri.fyp.demo.roboflow.data.entity.workflow.ImageData}.
     *
     * @return The converted entity object.
     */
    public ImageData toEntity() {
        ImageData image = new ImageData();
        image.setWidth(width);
        image.setHeight(height);
        return image;
    }
}