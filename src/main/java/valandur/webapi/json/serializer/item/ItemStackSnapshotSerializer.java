package valandur.webapi.json.serializer.item;

import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class ItemStackSnapshotSerializer extends WebAPIBaseSerializer<ItemStackSnapshot> {
    @Override
    public void serialize(ItemStackSnapshot value) throws IOException {
        writeValue(value.createStack());
    }
}
