package valandur.webapi.json.serializers.general;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.item.inventory.ItemStack;

import java.io.IOException;

public class ItemStackSerializer extends StdSerializer<ItemStack> {

    public ItemStackSerializer() {
        this(null);
    }

    public ItemStackSerializer(Class<ItemStack> t) {
        super(t);
    }

    @Override
    public void serialize(ItemStack value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("id", value.getItem().getId());
        gen.writeNumberField("quantity", value.getQuantity());
        gen.writeEndObject();
    }
}
