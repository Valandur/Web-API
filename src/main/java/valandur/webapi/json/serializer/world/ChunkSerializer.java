package valandur.webapi.json.serializer.world;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.world.Chunk;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class ChunkSerializer extends WebAPISerializer<Chunk>{
    @Override
    public void serialize(Chunk value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "uuid", value.getUniqueId());
        writeField(provider, "world", value.getWorld());
        writeField(provider, "position", value.getPosition());
        writeField(provider, "isPopulated", value.isPopulated());
        writeField(provider, "isLoaded", value.isLoaded());
        gen.writeEndObject();
    }
}
