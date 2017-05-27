package valandur.webapi.json.serializer.block;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.block.BlockUpdate;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class BlockUpdateSerializer  extends WebAPISerializer<BlockUpdate>{
    @Override
    public void serialize(BlockUpdate value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "uuid", value.getUUID());
        writeField(provider, "status", value.getStatus().toString());
        writeField(provider, "progress", value.getProgress());
        writeField(provider, "blocksSet", value.getBlocksSet());
        gen.writeEndObject();
    }
}
