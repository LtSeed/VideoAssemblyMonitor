package nusri.fyp.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import nusri.fyp.demo.roboflow.data.AnyData;
import nusri.fyp.demo.roboflow.data.RoboflowResponseData;
import nusri.fyp.demo.roboflow.data.response.WorkflowInferenceResponse;

import java.util.List;

/**
 * DTO for the Workflow Inference Response, implementing {@link RoboflowResponseData}.
 */
@Data
public class WorkflowInferenceResponseDTO implements RoboflowResponseData {
    /**
     * A list of output data from the workflow.
     */
    private List<WorkflowOutputDataDTO> outputs;

    /**
     * Profiler trace information for the inference workflow.
     */
    @JsonProperty("profiler_trace")
    private AnyData profilerTrace;

    /**
     * Converts this DTO to an entity {@link WorkflowInferenceResponse}.
     *
     * @return The converted entity object.
     */
    public WorkflowInferenceResponse toEntity(){
        WorkflowInferenceResponse response = new WorkflowInferenceResponse();
        response.setOutputs(outputs.stream().map(WorkflowOutputDataDTO::toEntity).toList());
        response.setProfilerTrace(profilerTrace);
        return response;
    }
}