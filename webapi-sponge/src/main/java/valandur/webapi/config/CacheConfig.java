package valandur.webapi.config;

import com.google.common.collect.Lists;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public class CacheConfig extends BaseConfig {

    @Setting(comment = "The number of entries that are saved. This defines how \"far back\" the history goes.")
    public int chat_amount = 100;

    @Setting(comment = "The number of entries that are saved. This defines how \"far back\" the history goes.")
    public int cmd_amount = 100;

    @Setting(comment = "The number of seconds that the different types of data is cached for")
    public Map<String, Long> duration = new HashMap<>();

    @Setting(comment = "The folders in which Web-API looks for other plugins.")
    public List<String> pluginFolders = Lists.newArrayList("./mods", "./plugins");

    @Setting(comment = "These are commands that should not show up in the command log.\n" +
            "For example if you have a second auth plugin, or something where\n" +
            "players enter private data, put the command here, so that it's\n" +
            "filtered from the logs, and also won't show up in the admin panel.")
    public List<String> censoredCommands = Lists.newArrayList("register", "reg", "login", "password", "pass");
}
