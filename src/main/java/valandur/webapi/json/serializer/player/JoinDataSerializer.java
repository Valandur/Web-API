package valandur.webapi.json.serializer.player;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.data.manipulator.mutable.entity.JoinData;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class JoinDataSerializer extends WebAPIBaseSerializer<JoinData> {
    @Override
    public void serialize(JoinData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "first", value.firstPlayed().get().getEpochSecond());
        writeField(provider, "last", value.lastPlayed().get().getEpochSecond());
        gen.writeEndObject();
    }
}
