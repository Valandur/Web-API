package valandur.webapi.json.serializers.player;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.data.manipulator.mutable.entity.JoinData;

import java.io.IOException;

public class JoinDataSerializer extends StdSerializer<JoinData> {

    public JoinDataSerializer() {
        this(null);
    }

    public JoinDataSerializer(Class<JoinData> t) {
        super(t);
    }

    @Override
    public void serialize(JoinData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("first", value.firstPlayed().get().getEpochSecond());
        gen.writeNumberField("last", value.lastPlayed().get().getEpochSecond());
        gen.writeEndObject();
    }
}
