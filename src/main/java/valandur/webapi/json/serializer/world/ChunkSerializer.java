package valandur.webapi.json.serializer.world;

import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Chunk;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class ChunkSerializer extends WebAPIBaseSerializer<Chunk> {
    @Override
    public void serialize(Chunk value) throws IOException {
        writeStartObject();
        writeField("uuid", value.getUniqueId());
        writeField("world", value.getWorld(), Tristate.FALSE);
        writeField("min", value.getBlockMin());
        writeField("max", value.getBlockMax());
        writeField("position", value.getPosition());
        writeField("isLoaded", value.isLoaded());
        writeEndObject();
    }
}
