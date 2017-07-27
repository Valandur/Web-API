package valandur.webapi.json.serializer.block;

import org.spongepowered.api.block.BlockSnapshot;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class BlockSnapshotSerializer extends WebAPIBaseSerializer<BlockSnapshot> {
    @Override
    public void serialize(BlockSnapshot value) throws IOException {
        writeStartObject();
        if (value.getCreator().isPresent()) writeField("creator", value.getCreator().get());
        if (value.getLocation().isPresent()) writeField("location", value.getLocation().get());
        writeField("state", value.getState());
        writeEndObject();
    }
}
