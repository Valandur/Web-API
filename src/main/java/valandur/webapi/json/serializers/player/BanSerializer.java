package valandur.webapi.json.serializers.player;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.util.ban.Ban;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class BanSerializer extends WebAPISerializer<Ban> {
    @Override
    public void serialize(Ban value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("createdOn", value.getCreationDate().getEpochSecond());
        if (value.getExpirationDate().isPresent()) gen.writeNumberField("expiresOn", value.getExpirationDate().get().getEpochSecond());
        if (value.getBanSource().isPresent()) gen.writeStringField("source", value.getBanSource().get().toString());
        gen.writeEndObject();
    }
}
