package valandur.webapi.json.serializers.block;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.blocks.BlockUpdate;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class BlockUpdateSerializer  extends WebAPISerializer<BlockUpdate>{
    @Override
    public void serialize(BlockUpdate value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("uuid", value.getUUID().toString());
        gen.writeStringField("status", value.getStatus().toString());
        gen.writeNumberField("progress", value.getProgress());
        gen.writeNumberField("blocksSet", value.getBlocksSet());
        gen.writeEndObject();
    }
}
