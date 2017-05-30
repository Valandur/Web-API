package valandur.webapi.json.serializer.item;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class ItemStackSnapshotSerializer extends WebAPISerializer<ItemStackSnapshot> {
    @Override
    public void serialize(ItemStackSnapshot value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "id", value.getType().getId());
        writeField(provider, "quantity", value.getCount());
        gen.writeEndObject();
    }
}
