package nusri.fyp.demo.repository;

import nusri.fyp.demo.entity.StateMachineLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * JPA repository interface for managing {@link StateMachineLog} entities, recording state machine runtime logs.
 * <br> Provides a method to query logs based on preset name.
 */
public interface StateMachineLogRepository extends JpaRepository<StateMachineLog, Long> {
    /**
     * Finds all state machine logs associated with a given preset name.
     *
     * @param presetName The name of the preset.
     * @return A list of {@link StateMachineLog} entities related to the preset.
     */
    List<StateMachineLog> findAllByPreset_Name(String presetName);
}