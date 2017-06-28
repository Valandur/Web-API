package valandur.webapi.json.serializer.tileentity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.data.manipulator.mutable.block.RedstonePoweredData;
import valandur.webapi.api.json.WebAPISerializer;

import java.io.IOException;

public class RedstonePoweredDataSerializer extends WebAPISerializer<RedstonePoweredData> {
    @Override
    public void serialize(RedstonePoweredData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        writeValue(provider, value.power().get());
    }
}
