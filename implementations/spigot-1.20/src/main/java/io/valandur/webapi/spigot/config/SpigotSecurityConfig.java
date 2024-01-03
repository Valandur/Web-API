package io.valandur.webapi.spigot.config;

import io.valandur.webapi.security.SecurityConfig;
import io.valandur.webapi.security.KeyPermissions;
import io.valandur.webapi.spigot.SpigotWebAPIPlugin;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class SpigotSecurityConfig implements SecurityConfig {

  private static final String path = "security.conf";

  private final SpigotWebAPIPlugin plugin;
  private File file;
  private FileConfiguration config;

  public SpigotSecurityConfig(SpigotWebAPIPlugin plugin) {
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
  public List<String> getWhitelist() {
    return this.get("whitelist", this.defaultWhitelist);
  }

  @Override
  public void setWhitelist(List<String> whitelist) {
    this.set("whitelist", whitelist);
  }

  @Override
  public List<String> getBlacklist() {
    return this.get("blacklist", this.defaultBlacklist);
  }

  @Override
  public void setBlacklist(List<String> blacklist) {
    this.set("blacklist", blacklist);
  }

  @Override
  public Map<String, KeyPermissions> getKeys() {
    return this.get("keys", this.defaultKeys);
  }

  @Override
  public void setKeys(Map<String, KeyPermissions> keys) {
    this.set("keys", keys);
  }

  private <T> T get(String path, T def) {
    return (T) config.get(path, def);
  }

  private <T> boolean set(String path, T value) {
    config.set(path, value);
    return true;
  }
}
