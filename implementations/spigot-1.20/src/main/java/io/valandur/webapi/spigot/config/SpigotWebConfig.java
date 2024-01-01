package io.valandur.webapi.spigot.config;

import io.valandur.webapi.web.WebConfig;
import io.valandur.webapi.spigot.SpigotWebAPIPlugin;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class SpigotWebConfig implements WebConfig {

  private static final String path = "server.conf";

  private final SpigotWebAPIPlugin plugin;
  private File file;
  private FileConfiguration config;

  public SpigotWebConfig(SpigotWebAPIPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void load() throws Exception {
    file = new File(plugin.getDataFolder(), path);
    config = new YamlConfiguration();

    if (!file.exists()) {
      file.getParentFile().mkdirs();
      plugin.saveResource(path, false);
    }

    config.load(file);
  }

  @Override
  public void save() throws Exception {
    config.save(file);
  }

  @Override
  public String getBasePath() {
    return this.get("path", this.defaultBasePath);
  }

  @Override
  public String getHost() {
    return this.get("host", this.defaultHost);
  }

  @Override
  public int getPort() {
    return this.get("port", this.defaultPort);
  }

  @Override
  public int getMinThreads() {
    return this.get("minThreads", this.defaultMinThreads);
  }

  @Override
  public int getMaxThreads() {
    return this.get("maxThreads", this.defaultMaxThreads);
  }

  @Override
  public int getIdleTimeout() {
    return this.get("idleTimeout", this.defaultIdleTimeout);
  }

  private <T> T get(String path, T def) {
    return (T) config.get(path, def);
  }
}
