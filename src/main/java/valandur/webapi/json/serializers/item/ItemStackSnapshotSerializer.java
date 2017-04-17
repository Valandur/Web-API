package valandur.webapi.json.serializers.item;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import valandur.webapi.json.JsonConverter;

import java.io.IOException;

public class ItemStackSnapshotSerializer extends StdSerializer<ItemStackSnapshot> {

    public ItemStackSnapshotSerializer() {
        this(null);
    }

    public ItemStackSnapshotSerializer(Class<ItemStackSnapshot> t) {
        super(t);
    }

    @Override
    public void serialize(ItemStackSnapshot value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("id", value.getType().getId());
        gen.writeNumberField("quantity", value.getCount());
        gen.writeEndObject();
    }
}
