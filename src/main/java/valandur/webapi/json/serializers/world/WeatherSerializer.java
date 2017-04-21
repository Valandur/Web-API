package valandur.webapi.json.serializers.world;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.world.weather.Weather;

import java.io.IOException;

public class WeatherSerializer extends StdSerializer<Weather> {

    public WeatherSerializer() {
        this(null);
    }

    public WeatherSerializer(Class<Weather> t) {
        super(t);
    }

    @Override
    public void serialize(Weather value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.getId());
    }
}
