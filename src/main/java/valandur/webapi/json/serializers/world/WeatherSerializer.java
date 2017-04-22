package valandur.webapi.json.serializers.world;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.world.weather.Weather;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class WeatherSerializer extends WebAPISerializer<Weather> {
    @Override
    public void serialize(Weather value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.getId());
    }
}
