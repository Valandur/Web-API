package valandur.webapi.json.serializer.block;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.trait.BlockTrait;
import valandur.webapi.api.json.WebAPISerializer;

import java.io.IOException;
import java.util.Map;

public class BlockStateSerializer extends WebAPISerializer<BlockState> {
    @Override
    public void serialize(BlockState value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "type", value.getType().getName());

        if (shouldWriteDetails(provider)) {
            gen.writeObjectFieldStart("data");
            for (Map.Entry<BlockTrait<?>, ?> entry : value.getTraitMap().entrySet()) {
                writeField(provider, entry.getKey().getName(), entry.getValue());
            }
            gen.writeEndObject();
        }

        gen.writeEndObject();
    }
}
