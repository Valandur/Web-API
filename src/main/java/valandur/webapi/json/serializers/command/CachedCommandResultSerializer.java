package valandur.webapi.json.serializers.command;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.cache.command.CachedCommandResult;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class CachedCommandResultSerializer extends WebAPISerializer<CachedCommandResult> {
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
