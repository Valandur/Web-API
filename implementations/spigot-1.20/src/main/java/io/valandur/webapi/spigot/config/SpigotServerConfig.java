package io.valandur.webapi.spigot.config;

import io.valandur.webapi.config.SecurityConfig;
import io.valandur.webapi.config.ServerConfig;
import io.valandur.webapi.security.KeyPermissions;
import io.valandur.webapi.spigot.SpigotWebAPIPlugin;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class SpigotServerConfig extends ServerConfig {

  private static final String path = "server.conf";

  private final SpigotWebAPIPlugin plugin;
  private File file;
  private FileConfiguration config;

  public SpigotServerConfig(SpigotWebAPIPlugin plugin) {
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
