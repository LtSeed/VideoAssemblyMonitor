package nusri.fyp.demo.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import nusri.fyp.demo.roboflow.data.entity.workflow.SinglePrediction;
import nusri.fyp.demo.state_machine.AbstractActionObservation;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Converter class that serializes and deserializes the {@code Map<Long, List<AbstractActionObservation>>}
 * to and from a JSON string for persistence in the database.
 * It uses Jackson's polymorphic serialization mechanism to automatically recognize and deserialize
 * objects into the correct subclass type, such as {@link SinglePrediction}.
 * This converter implements {@code AttributeConverter<Map<Long, List<AbstractActionObservation>>, String>}
 * and is invoked during the persistence and retrieval of data to and from the database.
 *
 * @see AttributeConverter
 * @see SinglePrediction
 * @see AbstractActionObservation
 */
@Slf4j
@Converter(autoApply = true)
public class ActionObservationConverter implements AttributeConverter<Map<Long, List<AbstractActionObservation>>, String> {

    /**
     * Jackson's object mapper used for serialization and deserialization.
     * <br> This instance allows the conversion between JSON and Java objects.
     */
    public final ObjectMapper objectMapper;


    /**
     * Constructor to initialize the converter with an {@link ObjectMapper}.
     *
     * @param mapper The {@link ObjectMapper} used for JSON serialization and deserialization.
     */
    ActionObservationConverter (ObjectMapper mapper) {
        this.objectMapper = mapper;
    }

    /**
     * Helper method to escape a string for JSON output, escaping only double quotes and backslashes.
     * <br> This is used to safely include the strings in the JSON structure.
     *
     * @param s The original string to be escaped.
     * @return A JSON-compatible escaped string, wrapped in double quotes.
     */
    private String toJsonString(String s) {
        if (s == null) {
            return "null";
        }
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    /**
     * Returns the serialization type identifier for the given {@link AbstractActionObservation} instance.
     * <br> This helps Jackson identify the specific subclass of {@link AbstractActionObservation} during
     * polymorphic deserialization.
     *
     * @param obs The {@link AbstractActionObservation} instance whose type is to be identified.
     * @return A string representing the type of the observation, e.g., "singlePrediction" or "actionObservation".
     */
    private String getTypeName(AbstractActionObservation obs) {
        if (obs instanceof SinglePrediction) {
            return "singlePrediction";
        }
        // else if (obs instanceof AnotherSubclass) ...
        return "actionObservation";
    }

    /**
     * Converts the entity field, which is a map of {@code Map<Long, List<AbstractActionObservation>>},
     * into a JSON string format to be stored in the database.
     * <br> The JSON string contains serialized {@link AbstractActionObservation} objects, and specific fields
     * are serialized based on the instance type (e.g., {@link SinglePrediction}).
     *
     * @param attribute The attribute value of type {@code Map<Long, List<AbstractActionObservation>>} to be converted.
     * @return The serialized JSON string representing the attribute value.
     */
    @Override
    public String convertToDatabaseColumn(Map<Long, List<AbstractActionObservation>> attribute) {
        if (attribute == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        boolean firstKey = true;
        for (Map.Entry<Long, List<AbstractActionObservation>> entry : attribute.entrySet()) {
            if (!firstKey) {
                sb.append(",");
            }
            firstKey = false;

            Long key = entry.getKey();
            List<AbstractActionObservation> list = entry.getValue();

            sb.append("\"").append(key).append("\":[");

            boolean firstObs = true;
            for (AbstractActionObservation obs : list) {
                if (!firstObs) {
                    sb.append(",");
                }
                firstObs = false;

                sb.append("{");

                sb.append("\"@type\":\"")
                        .append(getTypeName(obs))
                        .append("\",");

                sb.append("\"probability\":")
                        .append(obs.getProbability())
                        .append(",");

                sb.append("\"actionAndObject\":")
                        .append(toJsonString(obs.getActionAndObject()))
                        .append(",");

                if (obs instanceof SinglePrediction sp) {
                    sb.append("\"label\":")
                            .append(toJsonString(sp.getLabel()))
                            .append(",");
                    sb.append("\"confidence\":")
                            .append(sp.getConfidence())
                            .append(",");
                    sb.append("\"x\":")
                            .append(sp.getX())
                            .append(",");
                    sb.append("\"y\":")
                            .append(sp.getY())
                            .append(",");
                    sb.append("\"width\":")
                            .append(sp.getWidth())
                            .append(",");
                    sb.append("\"height\":")
                            .append(sp.getHeight())
                            .append(",");
                    sb.append("\"class_id\":")
                            .append(sp.getClassId())
                            .append(",");
                    sb.append("\"class\":")
                            .append(toJsonString(sp.getClazz()))
                            .append(",");
                    sb.append("\"detection_id\":")
                            .append(toJsonString(sp.getDetectionId()))
                            .append(",");
                    sb.append("\"parent_id\":")
                            .append(toJsonString(sp.getParentId()))
                            .append(",");
                }

                if (sb.charAt(sb.length() - 1) == ',') {
                    sb.deleteCharAt(sb.length() - 1);
                }
                sb.append("}");
            }
            sb.append("]");
        }

        sb.append("}");

        return sb.toString();
    }

    /**
     * Converts a JSON string stored in the database into a {@code Map<Long, List<AbstractActionObservation>>}.
     * <br> This method uses Jackson's {@link ObjectMapper} to deserialize the JSON string back into a map of
     * {@link AbstractActionObservation} objects, using the correct subclass for each observation.
     *
     * @param dbData The JSON string stored in the database representing the attribute value.
     * @return A {@code Map<Long, List<AbstractActionObservation>>} that corresponds to the deserialized JSON data.
     * @throws IllegalArgumentException If there is an error during deserialization or the data is invalid.
     */
    @Override
    public Map<Long, List<AbstractActionObservation>> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<>() {});
        } catch (IOException e) {
            throw new IllegalArgumentException("Error converting JSON to ActionObservation map", e);
        }
    }
}
