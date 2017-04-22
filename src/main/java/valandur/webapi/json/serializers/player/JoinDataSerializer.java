package valandur.webapi.json.serializers.player;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.data.manipulator.mutable.entity.JoinData;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class JoinDataSerializer extends WebAPISerializer<JoinData> {
    @Override
    public void serialize(JoinData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("first", value.firstPlayed().get().getEpochSecond());
        gen.writeNumberField("last", value.lastPlayed().get().getEpochSecond());
        gen.writeEndObject();
    }
}
