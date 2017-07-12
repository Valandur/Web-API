package valandur.webapi.json.serializer.block;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.block.BlockGetOperation;
import valandur.webapi.block.BlockOperation;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class BlockOperationSerializer extends WebAPIBaseSerializer<BlockOperation> {

    @Override
    public void serialize(BlockOperation value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        writeField(provider, "uuid", value.getUUID());
        writeField(provider, "status", value.getStatus().toString());
        writeField(provider, "progress", value.getProgress());
        writeField(provider, "estTimeRemaining", value.getEstimatedSecondsRemaining());
        writeField(provider, "link", "/block/op/" + value.getUUID());
        writeField(provider, "error", value.getError());

        if (shouldWriteDetails(provider)) {
            writeField(provider, "min", value.getMin());
            writeField(provider, "max", value.getMax());
            if (value instanceof BlockGetOperation) {
                writeField(provider, "blocks", ((BlockGetOperation)value).getBlocks());
            }
        }

        gen.writeEndObject();
    }
}
