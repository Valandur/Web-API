package valandur.webapi.json.serializer.block;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.block.BlockOperation;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class BlockUpdateSerializer  extends WebAPIBaseSerializer<BlockOperation> {
    @Override
    public void serialize(BlockOperation value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "uuid", value.getUUID());
        writeField(provider, "status", value.getStatus().toString());
        writeField(provider, "progress", value.getProgress());
        writeField(provider, "blocksSet", value.getBlocksProcessed());
        gen.writeEndObject();
    }
}
