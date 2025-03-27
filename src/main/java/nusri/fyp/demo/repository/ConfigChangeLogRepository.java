package nusri.fyp.demo.repository;

import nusri.fyp.demo.entity.ConfigChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for storing and querying {@link ConfigChangeLog} entities.
 * <br> Provides an additional method to retrieve the latest configuration change record.
 */
public interface ConfigChangeLogRepository extends JpaRepository<ConfigChangeLog, Long> {
    /**
     * Finds the configuration change record with the largest timestamp (i.e., the latest record).
     *
     * @return The latest {@link ConfigChangeLog}, or {@code null} if no record exists.
     */
    ConfigChangeLog findTopByOrderByTimestampDesc();
}
