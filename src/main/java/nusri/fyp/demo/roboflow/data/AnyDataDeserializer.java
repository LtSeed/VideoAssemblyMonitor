package nusri.fyp.demo.roboflow.data;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * Custom deserializer for the {@link AnyData} class.
 * <br> This class is responsible for deserializing JSON data into the {@link AnyData} object.
 * <br> It reads the entire JSON node as a string and stores it in the {@link AnyData} object.
 *
 * @author Liu Binghong
 * @since 1.0
 */
public class AnyDataDeserializer extends JsonDeserializer<AnyData> {

    /**
     * Deserializes JSON content into an {@link AnyData} object.
     *
     * @param p The parser used to read the JSON content.
     * @param context The deserialization context.
     * @return An {@link AnyData} object containing the raw JSON data as a string.
     * @throws IOException If an I/O error occurs while reading the JSON content.
     */
    @Override
    public AnyData deserialize(JsonParser p, DeserializationContext context) throws IOException {
        // Read the entire JSON node
        JsonNode node = p.readValueAsTree();
        // Convert the node to a string and return an AnyData instance
        return new AnyData(node.toString());
    }
}
