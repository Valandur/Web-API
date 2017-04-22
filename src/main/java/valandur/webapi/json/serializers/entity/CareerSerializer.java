package valandur.webapi.json.serializers.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.data.type.Career;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class CareerSerializer extends WebAPISerializer<Career> {
    @Override
    public void serialize(Career value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.getProfession().getId());
    }
}
