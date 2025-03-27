package nusri.fyp.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Object entity class used to describe object information with a unique ID.
 * <br> For example: id = 201, name = "door".
 */
@Entity
@Table(name = "objects")
@Data
public class ObjectWithId {
    /**
     * The object identifier, which is the primary key in the database.
     */
    @Id
    int id;

    /**
     * The name of the object. For example, "door", "book", etc.
     */
    String name;
}