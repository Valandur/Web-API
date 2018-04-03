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
}
