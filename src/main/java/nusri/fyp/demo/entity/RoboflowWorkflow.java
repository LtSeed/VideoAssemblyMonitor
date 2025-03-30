package nusri.fyp.demo.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "roboflow_workflow")
public class RoboflowWorkflow {

    @Id
    private String workflowId;

    private String workspaceName;

    private String workflowName;

    @Override
    public String toString() {
        return workspaceName + "@" + workflowName;
    }

}

