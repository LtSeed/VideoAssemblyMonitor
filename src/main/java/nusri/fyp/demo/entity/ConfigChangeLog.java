package nusri.fyp.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Configuration change log entity used to store the historical version information of system configurations.
 * <br> Includes a JSON string to store the complete configuration and a timestamp for sorting.
 */
@Entity
@Data
public class ConfigChangeLog {

    /**
     * The auto-incrementing primary key that uniquely identifies this log record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    /**
     * The serialized system configuration (in JSON format).
     */
    @Lob
    String config;

    /**
     * The timestamp that records when the change occurred.
     */
    long timestamp;
}
