package valandur.webapi.json.serializer.world;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.cache.world.CachedWorld;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class CachedWorldSerializer extends WebAPIBaseSerializer<CachedWorld> {
    @Override
    public void serialize(CachedWorld value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        writeField(provider, "uuid", value.getUUID());
        writeField(provider, "name", value.getName());
        writeField(provider, "link", value.getLink());

        if (shouldWriteDetails(provider)) {
            writeField(provider, "class", value.getClass().getName());
            writeField(provider, "isLoaded", value.isLoaded());
            writeField(provider, "loadOnStartup", value.doesLoadOnStartup());
            writeField(provider, "keepSpawnLoaded", value.doesKeepSpawnLoaded());
            writeField(provider, "allowCommands", value.doesAllowCommands());
            writeField(provider, "generateBonusChests", value.doesGenerateBonusChests());
            writeField(provider, "mapFeaturesEnabled", value.areMapFeaturesEnabled());
            writeField(provider, "border", value.getBorder());
            writeField(provider, "difficulty", value.getDifficulty());
            writeField(provider, "dimensionType", value.getDimensionType());
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
