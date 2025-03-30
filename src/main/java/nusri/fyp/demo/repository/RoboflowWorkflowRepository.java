package nusri.fyp.demo.repository;

import nusri.fyp.demo.entity.PythonServer;
import nusri.fyp.demo.entity.RoboflowWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoboflowWorkflowRepository extends JpaRepository<RoboflowWorkflow, String> {


    @Query("select p.workflowName from RoboflowWorkflow p")
    List<String> getAllRoboflowWorkflow();

    RoboflowWorkflow findByWorkspaceNameAndWorkflowName(String workspaceName, String workflowName);
}
