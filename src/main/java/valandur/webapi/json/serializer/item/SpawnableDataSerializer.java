package valandur.webapi.json.serializer.item;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.data.manipulator.mutable.item.SpawnableData;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class SpawnableDataSerializer extends WebAPIBaseSerializer<SpawnableData> {
    @Override
    public void serialize(SpawnableData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "id", value.type().get().getId());
        writeField(provider, "name", value.type().get().getTranslation().get());
        gen.writeEndObject();
    }
}
