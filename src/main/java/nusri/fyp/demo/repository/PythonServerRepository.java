package nusri.fyp.demo.repository;

import nusri.fyp.demo.entity.PythonServer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link PythonServer} entities.
 */
public interface PythonServerRepository extends JpaRepository<PythonServer, Long> {

    /**
     * Retrieves a {@link PythonServer} entity based on the specified host address and port number.
     * This method allows for the retrieval of a Python server instance using its host and port.
     * If no matching entity is found, the method returns {@code null}.
     *
     * @param host the host address of the Python server.
     * @param port the port number on which the Python server is listening.
     * @return the {@link PythonServer} entity matching the specified host and port, or {@code null} if no match is found.
     */
    PythonServer findByHostAndPort(String host, String port);
}
