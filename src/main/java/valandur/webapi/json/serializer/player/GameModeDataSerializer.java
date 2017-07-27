package valandur.webapi.json.serializer.player;

import org.spongepowered.api.data.manipulator.mutable.entity.GameModeData;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class GameModeDataSerializer extends WebAPIBaseSerializer<GameModeData> {
    @Override
    public void serialize(GameModeData value) throws IOException {
        writeValue(value.type().get());
    }
}
