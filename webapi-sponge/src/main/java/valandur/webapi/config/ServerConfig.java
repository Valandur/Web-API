package valandur.webapi.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ServerConfig extends BaseConfig {

    @Setting(comment = "The number of stat entries that are saved per stat. Together with\n" +
            "the stat interval this defines how far back the stats history goes.")
    public int maxStats = 4320;

    @Setting(comment = "The interval in seconds at which the server stats are recorded.")
    public int statsInterval = 5;
}
