package valandur.webapi.json.serializers.world;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.world.Dimension;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class DimensionSerializer extends WebAPISerializer<Dimension> {
    @Override
    public void serialize(Dimension value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("type", value.getType());
        gen.writeNumberField("height", value.getHeight());
        gen.writeNumberField("buildHeight", value.getBuildHeight());
        gen.writeEndObject();
    }
}
