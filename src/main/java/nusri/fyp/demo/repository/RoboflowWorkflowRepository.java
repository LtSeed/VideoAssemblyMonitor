package nusri.fyp.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

/**
 * Repository interface for managing {@link nusri.fyp.demo.entity.RoboflowWorkflow} entities.
 */
public interface RoboflowWorkflowRepository extends JpaRepository<nusri.fyp.demo.entity.RoboflowWorkflow, String> {

    /**
     * Retrieves the names of all workflows from the RoboflowWorkflow entities.
     * This method executes a JPQL query to select only the {@code workflowName} property from all available
     * {@link nusri.fyp.demo.entity.RoboflowWorkflow} records.
     *
     * @return a {@code List} of workflow names.
     */
    @Query("select p.workflowName from RoboflowWorkflow p")
    List<String> getAllRoboflowWorkflow();

    /**
     * Finds a {@link nusri.fyp.demo.entity.RoboflowWorkflow} entity by the specified workspace and workflow names.
     * This method searches for a workflow that exactly matches the given {@code workspaceName} and {@code workflowName}.
     * If no matching entity is found, the method returns {@code null}.
     *
     * @param workspaceName the name of the workspace associated with the workflow.
     * @param workflowName  the name of the workflow.
     * @return the {@link nusri.fyp.demo.entity.RoboflowWorkflow} entity matching the provided names, or {@code null} if not found.
     */
    nusri.fyp.demo.entity.RoboflowWorkflow findByWorkspaceNameAndWorkflowName(String workspaceName, String workflowName);
}
