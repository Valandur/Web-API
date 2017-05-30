package valandur.webapi.json.serializer.player;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.profile.GameProfile;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class GameProfileSerializer extends WebAPISerializer<GameProfile> {
    @Override
    public void serialize(GameProfile value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "uuid", value.getUniqueId());
        writeField(provider, "name", value.getName().orElse(null));
        gen.writeEndObject();
    }
}
