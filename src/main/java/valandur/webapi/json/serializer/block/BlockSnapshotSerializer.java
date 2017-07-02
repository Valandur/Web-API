package valandur.webapi.json.serializer.block;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.block.BlockSnapshot;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class BlockSnapshotSerializer extends WebAPIBaseSerializer<BlockSnapshot> {
    @Override
    public void serialize(BlockSnapshot value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        if (value.getCreator().isPresent()) writeField(provider, "creator", value.getCreator().get());
        if (value.getLocation().isPresent()) writeField(provider, "location", value.getLocation().get());
        writeField(provider, "state", value.getState());
        gen.writeEndObject();
    }
}
