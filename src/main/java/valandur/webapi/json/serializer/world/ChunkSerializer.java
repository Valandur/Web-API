package valandur.webapi.json.serializer.world;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Chunk;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class ChunkSerializer extends WebAPIBaseSerializer<Chunk> {
    @Override
    public void serialize(Chunk value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "uuid", value.getUniqueId());
        writeField(provider, "world", value.getWorld(), Tristate.FALSE);
        writeField(provider, "min", value.getBlockMin());
        writeField(provider, "max", value.getBlockMax());
        writeField(provider, "position", value.getPosition());
        writeField(provider, "isLoaded", value.isLoaded());
        gen.writeEndObject();
    }
}
