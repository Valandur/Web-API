package valandur.webapi.json.serializer.item;

import org.spongepowered.api.data.manipulator.mutable.item.DurabilityData;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class DurabilityDataSerializer extends WebAPIBaseSerializer<DurabilityData> {
    @Override
    public void serialize(DurabilityData value) throws IOException {
        writeStartObject();
        writeField("unbreakable", value.unbreakable().get());
        writeField("durability", value.durability().get());
        writeEndObject();
    }
}
