package valandur.webapi.json.serializer.tileentity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.data.manipulator.mutable.block.ConnectedDirectionData;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class ConnectedDirectionDataSerializer extends WebAPISerializer<ConnectedDirectionData> {
    @Override
    public void serialize(ConnectedDirectionData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        writeValue(provider, value.connectedDirections().get());
    }
}
