package valandur.webapi.json.serializer.item;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;
import java.util.Optional;

public class InventorySerializer extends WebAPISerializer<Inventory> {
    @Override
    public void serialize(Inventory value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "name", value.getName().get());
        writeField(provider, "stackCount", value.size());
        writeField(provider, "itemCount", value.totalItems());

        gen.writeArrayFieldStart("items");

        Iterable<Slot> slotIter = value.slots();
        for (Slot slot : slotIter) {
            Optional<ItemStack> stack = slot.peek();
            if (!stack.isPresent()) continue;

            writeValue(provider, stack.get());
        }
        gen.writeEndArray();

        gen.writeEndObject();
    }
}
