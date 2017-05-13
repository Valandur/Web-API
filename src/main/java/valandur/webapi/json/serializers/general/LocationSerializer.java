package valandur.webapi.json.serializers.general;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class LocationSerializer extends WebAPISerializer<Location<World>> {
    @Override
    public void serialize(Location<World> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("world", value.getExtent());
        gen.writeObjectField("position", value.getPosition());
        gen.writeEndObject();
    }
}
