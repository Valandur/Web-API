package valandur.webapi.json.serializers.player;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class GameModeSerializer extends WebAPISerializer<GameMode> {
    @Override
    public void serialize(GameMode value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.getId());
    }
}
