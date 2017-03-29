package valandur.webapi.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.util.ban.Ban;

import java.io.IOException;

public class BanSerializer extends StdSerializer<Ban> {

    public BanSerializer() {
        this(null);
    }

    public BanSerializer(Class<Ban> t) {
        super(t);
    }

    @Override
    public void serialize(Ban value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("createdOn", value.getCreationDate().getEpochSecond());
        if (value.getExpirationDate().isPresent()) gen.writeNumberField("expiresOn", value.getExpirationDate().get().getEpochSecond());
        if (value.getBanSource().isPresent()) gen.writeStringField("source", value.getBanSource().get().toString());
        gen.writeEndObject();
    }
}
