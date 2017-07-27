package valandur.webapi.json.serializer.tileentity;

import org.spongepowered.api.block.tileentity.TileEntity;
import valandur.webapi.WebAPI;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class TileEntitySerializer extends WebAPIBaseSerializer<TileEntity> {
    @Override
    public void serialize(TileEntity value) throws IOException {
        writeValue(WebAPI.getCacheService().getTileEntity(value.getLocation()).orElse(null));
    }
}
