package valandur.webapi.json.serializer.tileentity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.text.Text;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class SignDataSerializer extends WebAPISerializer<SignData> {
    @Override
    public void serialize(SignData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartArray();
        for (Text line : value.asList()) {
            writeValue(provider, line.toPlain());
        }
        gen.writeEndArray();
    }
}
