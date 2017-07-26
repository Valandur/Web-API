package valandur.webapi.json.serializer.player;

import org.spongepowered.api.profile.GameProfile;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class GameProfileSerializer extends WebAPIBaseSerializer<GameProfile> {
    @Override
    public void serialize(GameProfile value) throws IOException {
        writeStartObject();
        writeField("uuid", value.getUniqueId());
        writeField("name", value.getName().orElse(null));
        writeEndObject();
    }
}
