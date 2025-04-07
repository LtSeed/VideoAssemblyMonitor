package nusri.fyp.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a Python server instance used for remote or internal processing.
 * <p>
 * This entity holds the configuration details of a Python server, including its unique identifier, host address, and port number.
 * It is mapped to the "python_server" table in the database.
 * </p>
 */
@Entity
@Getter
@Setter
@Table(name = "python_server")
public class PythonServer {

    /**
     * The unique identifier of the Python server.
     * This field serves as the primary key for the entity.
     */
    @Id
    private long id;

    /**
     * The hostname or IP address of the Python server.
     */
    private String host;

    /**
     * The port number on which the Python server is listening.
     */
    private String port;

    /**
     * Returns a string representation of the Python server.
     * <p>
     * The returned string concatenates the host and port in the format "host@port".
     * </p>
     *
     * @return A string representation of the Python server.
     */
    @Override
    public String toString() {
        return host + "@" + port;
    }
}
