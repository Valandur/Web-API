package valandur.webapi.json.serializer.player;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.data.manipulator.mutable.entity.GameModeData;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class GameModeDataSerializer extends WebAPISerializer<GameModeData> {
    @Override
    public void serialize(GameModeData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        writeValue(provider, value.type().get());
    }
}
