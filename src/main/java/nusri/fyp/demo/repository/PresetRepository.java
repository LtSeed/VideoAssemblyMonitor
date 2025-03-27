package nusri.fyp.demo.repository;

import nusri.fyp.demo.entity.Preset;
import nusri.fyp.demo.entity.StateMachineLog;
import org.hibernate.annotations.SQLSelect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * JPA repository interface for managing {@link Preset} entities.
 * <br> Provides custom query methods, including finding presets by name and retrieving all preset names.
 */
public interface PresetRepository extends JpaRepository<Preset, Long> {
    /**
     * Finds presets by their exact name.
     *
     * @param name The preset name.
     * @return A list of {@link Preset} entities that match the given name.
     */
    List<Preset> findPresetByName(String name);

    /**
     * Retrieves all preset names in the system.
     *
     * @return A list of preset names.
     */
    @Query("select p.name from Preset p")
    List<String> findAllPresetNames();
}