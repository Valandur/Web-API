package valandur.webapi.json.serializer.item;

import org.spongepowered.api.item.ItemType;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class ItemTypeSerializer extends WebAPIBaseSerializer<ItemType> {

    @Override
    protected void serialize(ItemType value) throws IOException {
        writeStartObject();
        writeField("id", value.getId());
        writeField("name", value.getTranslation().get());
        writeEndObject();
    }
}
