package valandur.webapi.json.serializer.world;

import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.cache.world.CachedWorld;

import java.io.IOException;

public class CachedWorldSerializer extends WebAPIBaseSerializer<CachedWorld> {
    @Override
    public void serialize(CachedWorld value) throws IOException {
        writeStartObject();

        writeField("uuid", value.getUUID());
        writeField("name", value.getName());
        writeField("link", value.getLink());

        if (shouldWriteDetails()) {
            writeField("isLoaded", value.isLoaded());
            writeField("loadOnStartup", value.doesLoadOnStartup());
            writeField("keepSpawnLoaded", value.doesKeepSpawnLoaded());
            writeField("allowCommands", value.doesAllowCommands());
            writeField("generateBonusChests", value.doesGenerateBonusChests());
            writeField("mapFeaturesEnabled", value.areMapFeaturesEnabled());
            writeField("border", value.getBorder());
            writeField("difficulty", value.getDifficulty());
            writeField("dimensionType", value.getDimensionType());
            writeField("gameMode", value.getGameMode());
            writeField("gameRules", value.getGameRules());
            writeField("generatorType", value.getGeneratorType());
            writeField("seed", value.getSeed());
            writeField("spawn", value.getSpawn());
            writeField("time", value.getTime());
            writeField("weather", value.getWeather());
        }

        writeEndObject();
    }
}
