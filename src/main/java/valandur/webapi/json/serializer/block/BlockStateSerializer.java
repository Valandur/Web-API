package valandur.webapi.json.serializer.block;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.trait.BlockTrait;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;
import java.util.Map;

public class BlockStateSerializer extends WebAPIBaseSerializer<BlockState> {
    @Override
    public void serialize(BlockState value) throws IOException {
        writeStartObject();
        writeField("type", value.getType().getName());

        if (shouldWriteDetails()) {
            writeObjectFieldStart("data");
            for (Map.Entry<BlockTrait<?>, ?> entry : value.getTraitMap().entrySet()) {
                writeField(entry.getKey().getName(), entry.getValue());
            }
            writeEndObject();
        }

        writeEndObject();
    }
}
