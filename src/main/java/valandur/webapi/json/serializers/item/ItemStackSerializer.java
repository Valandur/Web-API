package valandur.webapi.json.serializers.item;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.json.JsonConverter;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class ItemStackSerializer extends WebAPISerializer<ItemStack> {
    @Override
    public void serialize(ItemStack value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("id", value.getItem().getId());
        gen.writeNumberField("quantity", value.getQuantity());
        gen.writeObjectField("data", JsonConverter.dataHolderToJson(value));
        gen.writeEndObject();
    }
}
