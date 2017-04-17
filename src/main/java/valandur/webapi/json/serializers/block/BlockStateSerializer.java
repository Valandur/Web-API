package valandur.webapi.json.serializers.block;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.trait.BlockTrait;

import java.io.IOException;
import java.util.Map;

public class BlockStateSerializer extends StdSerializer<BlockState> {

    public BlockStateSerializer() {
        this(null);
    }

    public BlockStateSerializer(Class<BlockState> t) {
        super(t);
    }

    @Override
    public void serialize(BlockState value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("type", value.getType().getName());

        gen.writeObjectFieldStart("data");
        for (Map.Entry<BlockTrait<?>, ?> entry : value.getTraitMap().entrySet()) {
            gen.writeObjectField(entry.getKey().getName(), entry.getValue());
        }
        gen.writeEndObject();
        
        gen.writeEndObject();
    }
}
