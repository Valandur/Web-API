package valandur.webapi.json.serializers.world;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.world.difficulty.Difficulty;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class DifficultySerializer extends WebAPISerializer<Difficulty> {
    @Override
    public void serialize(Difficulty value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.getId());
    }
}
