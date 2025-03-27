package nusri.fyp.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Action entity class used to describe action information with a unique ID.
 * <br> For example: id = 101, name = "open".
 */
@Entity
@Table(name = "actions")
@Data
public class ActionWithId {
    /**
     * The action identifier, which is the primary key in the database.
     */
    @Id
    int id;

    /**
     * The name of the action. For example, "open", "close", etc.
     */
    String name;
}