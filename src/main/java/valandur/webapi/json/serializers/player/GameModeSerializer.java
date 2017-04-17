package valandur.webapi.json.serializers.player;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;

import java.io.IOException;

public class GameModeSerializer extends StdSerializer<GameMode> {

    public GameModeSerializer() {
        this(null);
    }

    public GameModeSerializer(Class<GameMode> t) {
        super(t);
    }

    @Override
    public void serialize(GameMode value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.getId());
    }
}
