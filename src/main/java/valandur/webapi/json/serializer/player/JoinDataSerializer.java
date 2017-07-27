package valandur.webapi.json.serializer.player;

import org.spongepowered.api.data.manipulator.mutable.entity.JoinData;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class JoinDataSerializer extends WebAPIBaseSerializer<JoinData> {
    @Override
    public void serialize(JoinData value) throws IOException {
        writeStartObject();
        writeField("first", value.firstPlayed().get().getEpochSecond());
        writeField("last", value.lastPlayed().get().getEpochSecond());
        writeEndObject();
    }
}
