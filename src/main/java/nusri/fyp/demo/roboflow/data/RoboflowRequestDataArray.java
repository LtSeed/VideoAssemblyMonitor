package nusri.fyp.demo.roboflow.data;

import java.util.ArrayList;

/**
 * A generic class representing an array of request data objects for Roboflow API operations.
 * <br> This class extends {@link ArrayList} and implements {@link RoboflowResponseData}.
 * <br> It is used to hold a collection of request data objects of a specified type {@link T}.
 * <br> This class is primarily used to handle an array of request data in Roboflow API requests.
 *
 * @param <T> The type of data contained in this array.
 */
public class RoboflowRequestDataArray<T> extends ArrayList<T> implements RoboflowResponseData {
    // No additional functionality needed for this class, as it extends ArrayList and holds generic data.
}
