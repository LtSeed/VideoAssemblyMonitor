
package nusri.fyp.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite key class identifying a node in a {@link Preset}.
 * <br> Composed of {@code preset} and {@code number}, ensuring uniqueness.
 */
@Embeddable
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PresetNodeId implements Serializable {

    /**
     * The associated preset, maintained with a {@code @ManyToOne} relationship to {@link Preset}.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preset_id")  // Foreign key linking to Preset
    @JsonBackReference
    private Preset preset;  // Associated Preset entity

    /**
     * The node number, identifying the step order or ID in the process.
     */
    private int number;

    /**
     * No-arg constructor (required by JPA).
     */
    public PresetNodeId() {
    }

    /**
     * Constructor to initialize the relationship with the specified preset and node number.
     *
     * @param preset The preset entity.
     * @param number The node number.
     */
    public PresetNodeId(Preset preset, int number) {
        this.preset = preset;
        this.number = number;
    }

    /**
     * Compares two composite keys for equality.
     *
     * @param o The other object.
     * @return True if the preset and node number are the same.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PresetNodeId that = (PresetNodeId) o;
        return number == that.number && Objects.equals(preset, that.preset);
    }

    /**
     * Computes the hash code of the composite key.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(preset, number);
    }

    /**
     * For debugging purposes, outputs the preset name and number information.
     *
     * @return A string representation.
     */
    @Override
    public String toString() {
        return this.preset.getId() + "." + this.number;
    }
}