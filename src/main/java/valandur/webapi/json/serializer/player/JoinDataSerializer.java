package valandur.webapi.json.serializer.player;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.data.manipulator.mutable.entity.JoinData;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class JoinDataSerializer extends WebAPISerializer<JoinData> {
    @Override
    public void serialize(JoinData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "first", value.firstPlayed().get().getEpochSecond());
        writeField(provider, "last", value.lastPlayed().get().getEpochSecond());
        gen.writeEndObject();
    }
}
