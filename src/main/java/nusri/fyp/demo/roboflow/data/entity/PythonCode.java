package nusri.fyp.demo.roboflow.data.entity;

import lombok.Data;
import java.util.List;

/**
 * Represents Python code with associated function details.
 * <br> This class is used to encapsulate the Python code and its associated function names used in the workflow processing.
 */
@Data
public class PythonCode {

    /**
     * The type of Python code.
     * <br> This specifies the type or category of Python code (e.g., script, function, etc.).
     */
    private String type;

    /**
     * The code for the function to run.
     * <br> This contains the Python code that defines the function to execute in the workflow.
     */
    private String runFunctionCode;

    /**
     * The name of the function to run.
     * <br> This represents the name of the Python function that will be executed in the workflow.
     */
    private String runFunctionName;

    /**
     * The code for the initialization function.
     * <br> This contains the Python code that defines any setup or initialization needed before the main function is executed.
     */
    private String initFunctionCode;

    /**
     * The name of the initialization function.
     * <br> This represents the name of the Python function that initializes or sets up any necessary components.
     */
    private String initFunctionName;

    /**
     * The list of imports used in the Python code.
     * <br> This contains a list of the Python modules or packages that need to be imported to run the code.
     */
    private List<String> imports;
}
