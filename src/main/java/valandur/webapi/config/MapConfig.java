package valandur.webapi.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class MapConfig extends BaseConfig {

    @Setting(comment = "This contains the mapping of biome ids to colors used in the map.\n" +
            "Colors are specified as 6 digit hex codes (like in html/css).\n" +
            "If a biome cannot be found then white (\"000000\") is used.\n" +
            "If a biome contains \"_mutated\" in the id then the non-mutated version is used if a mutated version is not defined.")
    public Map<String, String> biomeColors = new HashMap<>();

    // Add default map values
    public MapConfig() {
        biomeColors.put("minecraft:ocean", "000070");
        biomeColors.put("minecraft:plains", "8DB360");
        biomeColors.put("minecraft:desert", "FA9418");
        biomeColors.put("minecraft:extreme_hills", "606060");
        biomeColors.put("minecraft:forest", "056621");
        biomeColors.put("minecraft:taiga", "0B6659");
        biomeColors.put("minecraft:swampland", "07F9B2");
        biomeColors.put("minecraft:river", "0000FF");
        biomeColors.put("minecraft:hell", "FF0000");
        biomeColors.put("minecraft:sky", "8080FF");
        biomeColors.put("minecraft:frozen_ocean", "9090A0");
        biomeColors.put("minecraft:frozen_river", "A0A0FF");
        biomeColors.put("minecraft:ice_flats", "FFFFFF");
        biomeColors.put("minecraft:ice_mountains", "A0A0A0");
        biomeColors.put("minecraft:mushroom_island", "FF00FF");
        biomeColors.put("minecraft:mushroom_island_shore", "A000FF");
        biomeColors.put("minecraft:beaches", "FADE55");
        biomeColors.put("minecraft:desert_hills", "D25F12");
        biomeColors.put("minecraft:forest_hills", "22551C");
        biomeColors.put("minecraft:taiga_hills", "163933");
        biomeColors.put("minecraft:smaller_extreme_hills", "72789A");
        biomeColors.put("minecraft:jungle", "537B09");
        biomeColors.put("minecraft:jungle_hills", "2C4205");
        biomeColors.put("minecraft:jungle_edge", "628B17");
        biomeColors.put("minecraft:deep_ocean", "000030");
        biomeColors.put("minecraft:stone_beach", "A2A284");
        biomeColors.put("minecraft:cold_beach", "FAF0C0");
        biomeColors.put("minecraft:birch_forest", "307444");
        biomeColors.put("minecraft:birch_forest_hills", "1F5F32");
        biomeColors.put("minecraft:roofed_forest", "40511A");
        biomeColors.put("minecraft:taiga_cold", "31554A");
        biomeColors.put("minecraft:taiga_cold_hills", "243F36");
        biomeColors.put("minecraft:redwood_taiga", "596651");
        biomeColors.put("minecraft:redwood_taiga_hills", "545F3E");
        biomeColors.put("minecraft:extreme_hills_with_trees", "507050");
        biomeColors.put("minecraft:savanna", "BDB25F");
        biomeColors.put("minecraft:savanna_rock", "A79D64");
        biomeColors.put("minecraft:mesa", "D94515");
        biomeColors.put("minecraft:mesa_rock", "B09765");
        biomeColors.put("minecraft:mesa_clear_rock", "CA8C65");
    }
}
