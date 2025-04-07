package nusri.fyp.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a Roboflow workflow configuration.
 * <p>
 * This entity encapsulates details about a workflow from the Roboflow platform,
 * including its unique identifier, associated workspace name, and the workflow name.
 * The entity is mapped to the "roboflow_workflow" table in the database.
 * </p>
 */
@Entity
@Getter
@Setter
@Table(name = "roboflow_workflow")
public class RoboflowWorkflow {

    /**
     * The unique identifier of the workflow.
     * This field serves as the primary key for the entity.
     */
    @Id
    private String workflowId;

    /**
     * The name of the workspace associated with the workflow.
     */
    private String workspaceName;

    /**
     * The name of the workflow.
     */
    private String workflowName;

    /**
     * Returns a string representation of the Roboflow workflow.
     * <p>
     * The returned string concatenates the workspace name and workflow name in the format "workspaceName@workflowName".
     * </p>
     *
     * @return A string representation of the Roboflow workflow.
     */
    @Override
    public String toString() {
        return workspaceName + "@" + workflowName;
    }
}
