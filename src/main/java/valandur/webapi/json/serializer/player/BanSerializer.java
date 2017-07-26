package valandur.webapi.json.serializer.player;

import org.spongepowered.api.util.ban.Ban;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class BanSerializer extends WebAPIBaseSerializer<Ban> {
    @Override
    public void serialize(Ban value) throws IOException {
        writeStartObject();
        writeField("createdOn", value.getCreationDate().getEpochSecond());
        if (value.getExpirationDate().isPresent()) writeField("expiresOn", value.getExpirationDate().get().getEpochSecond());
        if (value.getBanSource().isPresent()) writeField("source", value.getBanSource().get().toString());
        writeEndObject();
    }
}
