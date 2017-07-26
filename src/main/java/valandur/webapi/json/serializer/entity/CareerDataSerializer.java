package valandur.webapi.json.serializer.entity;

import org.spongepowered.api.data.manipulator.mutable.entity.CareerData;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class CareerDataSerializer extends WebAPIBaseSerializer<CareerData> {
    @Override
    public void serialize(CareerData value) throws IOException {
        writeValue(value.type().get());
    }
}
