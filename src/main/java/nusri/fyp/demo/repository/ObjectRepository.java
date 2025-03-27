package nusri.fyp.demo.repository;

import nusri.fyp.demo.entity.ObjectWithId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * JPA repository interface for managing {@link ObjectWithId} entities.
 */
public interface ObjectRepository extends JpaRepository<ObjectWithId, Integer> {
    /**
     * Retrieves all object names.
     *
     * @return A list of object names.
     */
    @Query("select p.name from ObjectWithId p")
    List<String> getAllObjects();
}