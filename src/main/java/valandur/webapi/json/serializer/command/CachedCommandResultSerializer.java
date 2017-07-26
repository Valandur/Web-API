package valandur.webapi.json.serializer.command;

import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.cache.command.CachedCommandResult;

import java.io.IOException;

public class CachedCommandResultSerializer extends WebAPIBaseSerializer<CachedCommandResult> {
    @Override
    public void serialize(CachedCommandResult value) throws IOException {
        writeStartObject();
        writeField("affectedBlocks", value.getAffectedBlocks());
        writeField("affectedEntities", value.getAffectedEntities());
        writeField("affectedItems", value.getAffectedItems());
        writeField("queryResult", value.getQueryResult());
        writeField("successCount", value.getSuccessCount());
        writeEndObject();
    }
}
