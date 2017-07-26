package valandur.webapi.json.serializer.tileentity;

import org.spongepowered.api.data.manipulator.mutable.block.PoweredData;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class PoweredDataSerializer extends WebAPIBaseSerializer<PoweredData> {
    @Override
    public void serialize(PoweredData value) throws IOException {
        writeValue(value.powered().get());
    }
}
