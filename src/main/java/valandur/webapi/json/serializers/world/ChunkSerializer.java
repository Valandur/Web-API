package valandur.webapi.json.serializers.world;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.world.Chunk;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class ChunkSerializer extends WebAPISerializer<Chunk>{
    @Override
    public void serialize(Chunk value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("uuid", value.getUniqueId().toString());
        gen.writeObjectField("world", value.getWorld());
        gen.writeObjectField("position", value.getPosition());
        gen.writeBooleanField("isPopulated", value.isPopulated());
        gen.writeBooleanField("isLoaded", value.isLoaded());
        gen.writeEndObject();
    }
}
