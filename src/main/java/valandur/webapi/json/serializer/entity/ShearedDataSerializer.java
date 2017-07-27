package valandur.webapi.json.serializer.entity;

import org.spongepowered.api.data.manipulator.mutable.entity.ShearedData;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class ShearedDataSerializer extends WebAPIBaseSerializer<ShearedData> {
    @Override
    public void serialize(ShearedData value) throws IOException {
        writeValue(value.sheared().get());
    }
}
