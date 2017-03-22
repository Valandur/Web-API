package valandur.webapi.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.profile.GameProfile;

import java.io.IOException;

public class GameProfileSerializer extends StdSerializer<GameProfile> {

    public GameProfileSerializer() {
        this(null);
    }

    public GameProfileSerializer(Class<GameProfile> t) {
        super(t);
    }

    @Override
    public void serialize(GameProfile value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("uuid", value.getUniqueId().toString());
        gen.writeStringField("name", value.getName().orElse(null));
        gen.writeEndObject();
    }
}
