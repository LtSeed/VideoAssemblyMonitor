package nusri.fyp.demo.roboflow.data.entity;

import lombok.Data;

/**
 * Represents a 2D point.
 * <br> This class encapsulates the coordinates of a point in a 2D space, along with a boolean flag to indicate
 * whether the point is considered "positive" (for example, in the context of a classification or marking).
 */
@Data
public class Point {

    /**
     * The x-coordinate of the point.
     * <br> This represents the horizontal position of the point in a 2D space.
     */
    private double x;

    /**
     * The y-coordinate of the point.
     * <br> This represents the vertical position of the point in a 2D space.
     */
    private double y;

    /**
     * A boolean flag indicating whether the point is positive.
     * <br> This flag could be used in specific scenarios, such as indicating if the point belongs to a certain
     * category or has a positive classification.
     */
    private boolean positive;
}
