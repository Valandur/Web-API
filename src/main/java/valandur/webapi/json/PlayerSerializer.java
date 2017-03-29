package valandur.webapi.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.entity.living.player.Player;
import valandur.webapi.cache.DataCache;

import java.io.IOException;

public class PlayerSerializer extends StdSerializer<Player> {

    public PlayerSerializer() {
        this(null);
    }

    public PlayerSerializer(Class<Player> t) {
        super(t);
    }

    @Override
    public void serialize(Player value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        boolean details = provider.isEnabled(MapperFeature.AUTO_DETECT_CREATORS);
        gen.writeRawValue(JsonConverter.toString(DataCache.getPlayer(value), details));
    }
}
