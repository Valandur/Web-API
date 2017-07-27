package valandur.webapi.json.serializer.block;

import valandur.webapi.block.BlockGetOperation;
import valandur.webapi.block.BlockOperation;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class BlockOperationSerializer extends WebAPIBaseSerializer<BlockOperation> {

    @Override
    public void serialize(BlockOperation value) throws IOException {
        writeStartObject();

        writeField("uuid", value.getUUID());
        writeField("status", value.getStatus().toString());
        writeField("progress", value.getProgress());
        writeField("estTimeRemaining", value.getEstimatedSecondsRemaining());
        writeField("link", "/block/op/" + value.getUUID());
        writeField("error", value.getError());

        if (shouldWriteDetails()) {
            writeField("min", value.getMin());
            writeField("max", value.getMax());
            if (value instanceof BlockGetOperation) {
                writeField("blocks", ((BlockGetOperation)value).getBlocks());
            }
        }

        writeEndObject();
    }
}
