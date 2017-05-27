package valandur.webapi.json.serializer.item;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.data.manipulator.mutable.item.SpawnableData;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class SpawnableDataSerializer extends WebAPISerializer<SpawnableData> {
    @Override
    public void serialize(SpawnableData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        writeValue(provider, value.type().get().getId());
    }
}
