package valandur.webapi.json.serializer.item;

import org.spongepowered.api.data.manipulator.mutable.item.SpawnableData;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class SpawnableDataSerializer extends WebAPIBaseSerializer<SpawnableData> {
    @Override
    public void serialize(SpawnableData value) throws IOException {
        writeStartObject();
        writeField("id", value.type().get().getId());
        writeField("name", value.type().get().getTranslation().get());
        writeEndObject();
    }
}
