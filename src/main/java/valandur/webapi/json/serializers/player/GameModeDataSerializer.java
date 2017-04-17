package valandur.webapi.json.serializers.player;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.data.manipulator.mutable.entity.GameModeData;

import java.io.IOException;

public class GameModeDataSerializer extends StdSerializer<GameModeData> {

    public GameModeDataSerializer() {
        this(null);
    }

    public GameModeDataSerializer(Class<GameModeData> t) {
        super(t);
    }

    @Override
    public void serialize(GameModeData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeObject(value.type().get());
    }
}
