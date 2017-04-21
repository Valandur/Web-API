package valandur.webapi.json.serializers.world;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.world.difficulty.Difficulty;

import java.io.IOException;

public class DifficultySerializer extends StdSerializer<Difficulty> {

    public DifficultySerializer() {
        this(null);
    }

    public DifficultySerializer(Class<Difficulty> t) {
        super(t);
    }

    @Override
    public void serialize(Difficulty value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.getId());
    }
}
