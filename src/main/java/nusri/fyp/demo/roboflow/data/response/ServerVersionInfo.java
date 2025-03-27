package nusri.fyp.demo.roboflow.data.response;

import lombok.Data;
import nusri.fyp.demo.roboflow.data.RoboflowResponseData;

/**
 * Represents the version information of the server in the Roboflow API.
 * <br> This class contains details about the server's name, version, and unique identifier (UUID).
 */
@Data
public class ServerVersionInfo implements RoboflowResponseData {

    /**
     * The name of the server.
     * <br> This field represents the server's name or identifier.
     */
    private String name;

    /**
     * The version of the server.
     * <br> This field indicates the version of the server software.
     */
    private String version;

    /**
     * The unique identifier (UUID) of the server.
     * <br> This field provides a unique reference for the server instance.
     */
    private String uuid;

    /**
     * Returns a string representation of the {@link ServerVersionInfo} object.
     * <br> The string includes the server's name, version, and UUID.
     *
     * @return A string representation of the server version information.
     */
    @Override
    public String toString() {
        return String.format("ServerVersionInfo [name=%s, version=%s, uuid=%s]", name, version, uuid);
    }
}
