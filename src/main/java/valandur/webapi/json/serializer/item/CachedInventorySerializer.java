package valandur.webapi.json.serializer.item;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.cache.misc.CachedInventory;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class CachedInventorySerializer extends WebAPIBaseSerializer<CachedInventory> {
    @Override
    public void serialize(CachedInventory value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "type", value.getType());
        writeField(provider, "name", value.getName());
        writeField(provider, "capacity", value.getCapacity());
        writeField(provider, "totalItems", value.getTotalItems());

        gen.writeArrayFieldStart("items");
        for (ItemStack stack : value.getItems()) {
            writeValue(provider, stack);
        }
        gen.writeEndArray();
        gen.writeEndObject();
    }
}
