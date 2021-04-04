package io.valandur.webapi.config;

import io.valandur.webapi.SpigotWebAPIPlugin;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class SpigotConfig extends Config {

    private final SpigotWebAPIPlugin plugin;
    private File file;
    private FileConfiguration config;

    public SpigotConfig(String name, SpigotWebAPIPlugin plugin) {
        super(name);

        this.plugin = plugin;
    }

    @Override
    public void load() {
        file = new File(plugin.getDataFolder(), name);
        config = new YamlConfiguration();

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(name, false);
        }

        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> T get(String path, T def) {
        return (T) config.get(path, def);
    }
}
