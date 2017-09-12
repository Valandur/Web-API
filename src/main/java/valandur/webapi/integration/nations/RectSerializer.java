package valandur.webapi.integration.nations;

import com.arckenver.nations.object.Rect;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class RectSerializer extends WebAPIBaseSerializer<Rect> {

    @Override
    protected void serialize(Rect value) throws IOException {
        writeStartObject();
        writeField("world", cacheService.getWorld(value.getWorld()).orElse(null));
        writeField("minX", value.getMinX());
        writeField("maxX", value.getMaxX());
        writeField("minY", value.getMinY());
        writeField("maxY", value.getMaxY());
        writeEndObject();
    }
}
