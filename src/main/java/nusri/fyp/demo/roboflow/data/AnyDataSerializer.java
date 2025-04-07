package nusri.fyp.demo.roboflow.data;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Custom serializer for the {@link AnyData} class.
 * <br> This class is responsible for serializing an {@link AnyData} object into its raw JSON string representation.
 *
 * @author Liu Binghong
 * @since 1.0
 */
public class AnyDataSerializer extends JsonSerializer<AnyData> {

    /**
     * Serializes an {@link AnyData} object into its raw JSON string representation.
     *
     * @param value The {@link AnyData} object to be serialized.
     * @param gen The generator used to write the JSON content.
     * @param serializers The provider that can be used to get serializers for other types.
     * @throws IOException If an I/O error occurs during serialization.
     */
    @Override
    public void serialize(AnyData value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // Directly write the raw JSON string stored in the value object
        gen.writeRawValue(value.getData());
    }
}
