package valandur.webapi.json.serializers.item;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class ItemStackSnapshotSerializer extends WebAPISerializer<ItemStackSnapshot> {
    @Override
    public void serialize(ItemStackSnapshot value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("id", value.getType().getId());
        gen.writeNumberField("quantity", value.getCount());
        gen.writeEndObject();
    }
}
