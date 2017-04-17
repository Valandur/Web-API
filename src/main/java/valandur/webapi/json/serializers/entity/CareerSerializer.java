package valandur.webapi.json.serializers.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.data.type.Career;

import java.io.IOException;

public class CareerSerializer extends StdSerializer<Career> {

    public CareerSerializer() {
        this(null);
    }

    public CareerSerializer(Class<Career> t) {
        super(t);
    }

    @Override
    public void serialize(Career value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.getProfession().getId());
    }
}
