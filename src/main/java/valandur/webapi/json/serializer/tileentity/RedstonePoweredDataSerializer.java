package valandur.webapi.json.serializer.tileentity;

import org.spongepowered.api.data.manipulator.mutable.block.RedstonePoweredData;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class RedstonePoweredDataSerializer extends WebAPIBaseSerializer<RedstonePoweredData> {
    @Override
    public void serialize(RedstonePoweredData value) throws IOException {
        writeValue(value.power().get());
    }
}
