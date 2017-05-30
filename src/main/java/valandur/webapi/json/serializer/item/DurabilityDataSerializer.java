package valandur.webapi.json.serializer.item;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.data.manipulator.mutable.item.DurabilityData;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class DurabilityDataSerializer extends WebAPISerializer<DurabilityData> {
    @Override
    public void serialize(DurabilityData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "unbreakable", value.unbreakable().get());
        writeField(provider, "durability", value.durability().get());
        gen.writeEndObject();
    }
}
