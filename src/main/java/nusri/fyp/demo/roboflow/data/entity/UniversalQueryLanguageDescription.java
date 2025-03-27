package nusri.fyp.demo.roboflow.data.entity;

import lombok.Data;
import nusri.fyp.demo.roboflow.data.AnyData;

import java.util.List;

/**
 * A class representing a description of Universal Query Language (UQL).
 * <br> This class is used to describe the operations and operators available in the Universal Query Language (UQL).
 * <br> UQL allows for querying and interacting with data in a flexible way, supporting various operations and operators.
 */
@Data
public class UniversalQueryLanguageDescription {

    /**
     * A list of descriptions for the available operations in UQL.
     * <br> Each operation defines a specific action or task that can be performed within the UQL context.
     */
    private List<AnyData> operationsDescription;

    /**
     * A list of descriptions for the available operators in UQL.
     * <br> Each operator defines a specific function or behavior that can be applied to operands within UQL.
     */
    private List<AnyData> operatorsDescriptions;
}
