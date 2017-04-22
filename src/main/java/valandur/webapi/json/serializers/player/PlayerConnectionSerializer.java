package valandur.webapi.json.serializers.player;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.network.PlayerConnection;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class PlayerConnectionSerializer extends WebAPISerializer<PlayerConnection> {
    @Override
    public void serialize(PlayerConnection value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("address", value.getAddress().toString());
        gen.writeNumberField("latency", value.getLatency());
        gen.writeEndObject();
    }
}
