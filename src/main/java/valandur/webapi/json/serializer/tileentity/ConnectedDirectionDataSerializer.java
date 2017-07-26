package valandur.webapi.json.serializer.tileentity;

import org.spongepowered.api.data.manipulator.mutable.block.ConnectedDirectionData;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class ConnectedDirectionDataSerializer extends WebAPIBaseSerializer<ConnectedDirectionData> {
    @Override
    public void serialize(ConnectedDirectionData value) throws IOException {
        writeValue(value.connectedDirections().get());
    }
}
