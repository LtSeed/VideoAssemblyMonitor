package nusri.fyp.demo.roboflow.data.entity;

import lombok.Data;

/**
 * Represents a bounding box in a 2D space.
 * <br> This class is typically used to describe the coordinates and dimensions of a rectangular area within an image,
 * such as the bounding box around a detected object in an object detection task.
 *
 * @author Liu Binghong
 * @since 1.0
 */
@Data
public class Box {

    /**
     * The x-coordinate of the top-left corner of the bounding box.
     * <br> This value represents the horizontal position of the top-left corner of the box within the 2D space.
     */
    private double x;

    /**
     * The y-coordinate of the top-left corner of the bounding box.
     * <br> This value represents the vertical position of the top-left corner of the box within the 2D space.
     */
    private double y;

    /**
     * The width of the bounding box.
     * <br> This value represents the horizontal extent of the box, measured from the left to the right side.
     */
    private double width;

    /**
     * The height of the bounding box.
     * <br> This value represents the vertical extent of the box, measured from the top to the bottom side.
     */
    private double height;
}
