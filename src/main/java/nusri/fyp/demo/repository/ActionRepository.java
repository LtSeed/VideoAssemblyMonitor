package nusri.fyp.demo.repository;

import nusri.fyp.demo.entity.ActionWithId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * JPA repository interface for managing {@link ActionWithId} entities.
 * <br> Extends {@code JpaRepository<ActionWithId, Integer>} and provides default operations such as:
 * <ul>
 *   <li>Finding and deleting by the primary key {@code id}</li>
 *   <li>Basic methods like save, batch save, etc.</li>
 * </ul>
 */
public interface ActionRepository extends JpaRepository<ActionWithId, Integer> {
    /**
     * Retrieves all action names.
     *
     * @return A list of action names.
     */
    @Query("select p.name from ActionWithId p")
    List<String> getAllActions();
}
