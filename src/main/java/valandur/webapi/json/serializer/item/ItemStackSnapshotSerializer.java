package valandur.webapi.json.serializer.item;

import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class ItemStackSnapshotSerializer extends WebAPIBaseSerializer<ItemStackSnapshot> {
    @Override
    public void serialize(ItemStackSnapshot value) throws IOException {
        writeStartObject();
        writeField("id", value.getType().getId());
        writeField("quantity", value.getCount());
        writeEndObject();
    }
}
