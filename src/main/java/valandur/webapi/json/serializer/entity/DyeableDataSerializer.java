package valandur.webapi.json.serializer.entity;

import org.spongepowered.api.data.manipulator.mutable.DyeableData;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class DyeableDataSerializer extends WebAPIBaseSerializer<DyeableData> {
    @Override
    public void serialize(DyeableData value) throws IOException {
        writeValue(value.type().get().getId());
    }
}
