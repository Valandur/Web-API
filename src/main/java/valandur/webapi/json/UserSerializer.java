package valandur.webapi.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

import java.io.IOException;

public class UserSerializer extends StdSerializer<User> {

    public UserSerializer() {
        this(null);
    }

    public UserSerializer(Class<User> t) {
        super(t);
    }

    @Override
    public void serialize(User value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("uuid", value.getUniqueId().toString());
        gen.writeStringField("name", value.getName());
        gen.writeEndObject();
    }
}
