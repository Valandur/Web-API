package valandur.webapi.json.serializer.player;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.util.ban.Ban;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class BanSerializer extends WebAPISerializer<Ban> {
    @Override
    public void serialize(Ban value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "createdOn", value.getCreationDate().getEpochSecond());
        if (value.getExpirationDate().isPresent()) writeField(provider, "expiresOn", value.getExpirationDate().get().getEpochSecond());
        if (value.getBanSource().isPresent()) writeField(provider, "source", value.getBanSource().get().toString());
        gen.writeEndObject();
    }
}
