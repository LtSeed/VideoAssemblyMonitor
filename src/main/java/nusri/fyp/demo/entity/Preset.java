package nusri.fyp.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Preset entity class representing the overall configuration of a process, including multiple nodes and related information.
 * <br> It corresponds to a table in the database and has a one-to-many relationship with {@link PresetNode}.
 */
@Entity
@Getter
@Setter
@Table(name = "preset")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Preset {
    /**
     * The auto-incrementing primary key that uniquely identifies this preset.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generate ID
    private Long id;

    /**
     * The name of the preset. For example: "DefaultPreset".
     */
    private String name;

    /**
     * The collection of associated nodes, each with its order and configuration in the process.
     * <br> In the database, it is related to {@link PresetNode} via a foreign key.
     */
    @OneToMany(mappedBy = "id.preset", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // Specify the foreign key relationship in PresetNode
    @JsonManagedReference
    private List<PresetNode> nodes = new ArrayList<>();

    /**
     * A method used for debugging, displaying the preset name and its associated nodes.
     *
     * @return A concatenated string representation.
     */
    @Override
    public String toString() {
        return "Preset{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nodes=[" + nodes.stream()
                .map(PresetNode::toString)
                .collect(Collectors.joining(", ")) +
                "]}";
    }

    /**
     * Retrieves a node by its ID.
     *
     * @param i The ID of the node.
     * @return The matching {@link PresetNode}.
     * @throws Throwable If the node with the given ID does not exist.
     */
    public PresetNode getNode(int i) throws Throwable {
        return this.getNodes().stream().filter(n -> n.getId().getNumber() == i).findFirst().orElseThrow((Supplier<Throwable>) () -> new NoSuchElementException("No such node with id " + i));
    }
}