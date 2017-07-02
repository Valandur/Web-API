package valandur.webapi.json.serializer.item;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class ItemStackSerializer extends WebAPIBaseSerializer<ItemStack> {
    @Override
    public void serialize(ItemStack value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        writeField(provider, "id", value.getItem().getId());
        writeField(provider, "name", value.getTranslation().get());
        writeField(provider, "quantity", value.getQuantity());

        if (shouldWriteDetails(provider)) {
            gen.writeObjectFieldStart("data");
            writeData(provider, value);
            gen.writeEndObject();
        }

        gen.writeEndObject();
    }
}
