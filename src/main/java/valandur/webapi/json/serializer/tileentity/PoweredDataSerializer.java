package valandur.webapi.json.serializer.tileentity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.data.manipulator.mutable.block.PoweredData;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class PoweredDataSerializer extends WebAPISerializer<PoweredData> {
    @Override
    public void serialize(PoweredData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        writeValue(provider, value.powered().get());
    }
}
