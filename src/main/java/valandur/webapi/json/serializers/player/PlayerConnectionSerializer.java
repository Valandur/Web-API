package valandur.webapi.json.serializers.player;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.network.PlayerConnection;

import java.io.IOException;

public class PlayerConnectionSerializer extends StdSerializer<PlayerConnection> {

    public PlayerConnectionSerializer() {
        this(null);
    }

    public PlayerConnectionSerializer(Class<PlayerConnection> t) {
        super(t);
    }

    @Override
    public void serialize(PlayerConnection value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("address", value.getAddress().toString());
        gen.writeNumberField("latency", value.getLatency());
        gen.writeEndObject();
    }
}
