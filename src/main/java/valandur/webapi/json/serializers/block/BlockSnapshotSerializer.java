package valandur.webapi.json.serializers.block;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.block.BlockSnapshot;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class BlockSnapshotSerializer extends WebAPISerializer<BlockSnapshot> {
    @Override
    public void serialize(BlockSnapshot value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        if (value.getCreator().isPresent()) gen.writeStringField("creator", value.getCreator().get().toString());
        if (value.getLocation().isPresent()) gen.writeObjectField("location", value.getLocation().get());
        gen.writeObjectField("state", value.getState());
        gen.writeEndObject();
    }
}
