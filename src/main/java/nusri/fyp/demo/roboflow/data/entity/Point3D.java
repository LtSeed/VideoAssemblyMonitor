package nusri.fyp.demo.roboflow.data.entity;

import lombok.Data;

/**
 * Represents a 3D point.
 * <br> This class encapsulates the coordinates of a point in a 3D space.
 */
@Data
public class Point3D {

    /**
     * The x-coordinate of the point in 3D space.
     * <br> This represents the horizontal position of the point in a 3D coordinate system.
     */
    private double x;

    /**
     * The y-coordinate of the point in 3D space.
     * <br> This represents the vertical position of the point in a 3D coordinate system.
     */
    private double y;

    /**
     * The z-coordinate of the point in 3D space.
     * <br> This represents the depth position of the point in a 3D coordinate system.
     */
    private double z;
}
