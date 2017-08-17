package valandur.webapi.json.serializer.world;

import org.spongepowered.api.util.Tristate;
import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.cache.world.CachedChunk;

import java.io.IOException;

public class CachedChunkSerializer extends WebAPIBaseSerializer<CachedChunk> {
    @Override
    public void serialize(CachedChunk value) throws IOException {
        writeStartObject();

        writeField("uuid", value.getUUID());
        writeField("position", value.getPosition());

        if (shouldWriteDetails()) {
            writeField("world", value.getWorld(), Tristate.FALSE);
            writeField("blockMin", value.getBlockMin());
            writeField("blockMax", value.getBlockMax());
            writeField("isLoaded", value.isLoaded());
            writeField("inhabitedTime", value.getInhabittedTime());
            writeField("difficultyFactor", value.getRegionalDifficultyFactor());
            writeField("difficultyPercentage", value.getRegionalDifficultyPercentage());
        }

        writeEndObject();
    }
}
