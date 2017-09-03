package valandur.webapi.json.serializer.item;

import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.cache.misc.CachedInventory;

import java.io.IOException;

public class CachedInventorySerializer extends WebAPIBaseSerializer<CachedInventory> {
    @Override
    public void serialize(CachedInventory value) throws IOException {
        writeStartObject();
        writeField("type", value.getType());
        writeField("name", value.getName());

        if (shouldWriteDetails()) {
            writeField("class", value.getObjectClass().getName());
            writeField("capacity", value.getCapacity());
            writeField("totalItems", value.getTotalItems());

            writeArrayFieldStart("items");
            for (ItemStack stack : value.getItems()) {
                writeValue(stack);
            }
            writeEndArray();
        }

        writeEndObject();
    }
}
