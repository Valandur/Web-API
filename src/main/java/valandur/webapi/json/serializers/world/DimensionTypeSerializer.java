package valandur.webapi.json.serializers.world;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.world.DimensionType;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class DimensionTypeSerializer extends WebAPISerializer<DimensionType> {
    @Override
    public void serialize(DimensionType value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("id", value.getId());
        gen.writeStringField("name", value.getName());
        gen.writeStringField("dimensionClass", value.getDimensionClass().getName());
        gen.writeEndObject();
    }
}
