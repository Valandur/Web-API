package valandur.webapi.json.serializer.world;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.cache.world.CachedWorld;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class CachedWorldSerializer extends WebAPISerializer<CachedWorld> {
    @Override
    public void serialize(CachedWorld value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        writeField(provider, "uuid", value.getUUID());
        writeField(provider, "name", value.getName());
        writeField(provider, "link", value.getLink());

        if (((AtomicBoolean)provider.getAttribute("details")).get()) {
            writeField(provider, "class", value.getClass().getName());
            writeField(provider, "border", value.getBorder());
            writeField(provider, "difficulty", value.getDifficulty());
            writeField(provider, "dimension", value.getDimension());
            writeField(provider, "gameMode", value.getGameMode());
            writeField(provider, "gameRules", value.getGameRules());
            writeField(provider, "generatorType", value.getGeneratorType());
            writeField(provider, "seed", value.getSeed());
            writeField(provider, "spawn", value.getSpawn());
            writeField(provider, "time", value.getTime());
            writeField(provider, "weather", value.getWeather());
        }

        gen.writeEndObject();
    }
}
