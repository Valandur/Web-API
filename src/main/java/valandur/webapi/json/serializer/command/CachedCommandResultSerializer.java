package valandur.webapi.json.serializer.command;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.cache.command.CachedCommandResult;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class CachedCommandResultSerializer extends WebAPIBaseSerializer<CachedCommandResult> {
    @Override
    public void serialize(CachedCommandResult value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "affectedBlocks", value.getAffectedBlocks());
        writeField(provider, "affectedEntities", value.getAffectedEntities());
        writeField(provider, "affectedItems", value.getAffectedItems());
        writeField(provider, "queryResult", value.getQueryResult());
        writeField(provider, "successCount", value.getSuccessCount());
        gen.writeEndObject();
    }
}
