package valandur.webapi.json.serializers.tileentity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.text.Text;

import java.io.IOException;

public class SignDataSerializer extends StdSerializer<SignData> {

    public SignDataSerializer() {
        this(null);
    }

    public SignDataSerializer(Class<SignData> t) {
        super(t);
    }

    @Override
    public void serialize(SignData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartArray();
        for (Text line : value.asList()) {
            gen.writeString(line.toPlain());
        }
        gen.writeEndArray();
    }
}
