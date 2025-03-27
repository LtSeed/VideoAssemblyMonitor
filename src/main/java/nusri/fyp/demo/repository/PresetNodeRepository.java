package nusri.fyp.demo.repository;

import nusri.fyp.demo.entity.PresetNode;
import nusri.fyp.demo.entity.PresetNodeId;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA repository interface for managing {@link PresetNode} entities.
 * <br> The primary key type is {@link PresetNodeId} (composite primary key).
 */
public interface PresetNodeRepository extends JpaRepository<PresetNode, PresetNodeId> {
}