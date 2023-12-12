package io.valandur.webapi.sponge.config;

import io.leangen.geantyref.TypeToken;
import io.valandur.webapi.config.SecurityConfig;
import io.valandur.webapi.security.KeyPermissions;
import io.valandur.webapi.sponge.SpongeWebAPIPlugin;
import java.util.Map;
import java.util.Set;
import org.spongepowered.api.Sponge;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

public class SpongeSecurityConfig extends SecurityConfig {

  private static final TypeToken<Set<String>> WHITELIST_TYPE = new TypeToken<>() {
  };
  private static final TypeToken<Set<String>> BLACKLIST_TYPE = new TypeToken<>() {
  };
  private static final TypeToken<Map<String, KeyPermissions>> KEYS_TYPE = new TypeToken<>() {
  };

  private final ConfigurationLoader<CommentedConfigurationNode> loader;
  private CommentedConfigurationNode node;

  public SpongeSecurityConfig(SpongeWebAPIPlugin plugin) {
    var serializers = Sponge.configManager().serializers().childBuilder()
        .register(TypeToken.get(KeyPermissions.class), new KeyPermissionsSerializer())
        .build();

    var path = plugin.getConfigPath().resolve("security.conf");
    var opts = ConfigurationOptions.defaults().serializers(serializers);
    loader = HoconConfigurationLoader.builder().defaultOptions(opts).prettyPrinting(true).path(path)
        .build();
    node = loader.createNode();
  }

  @Override
  public void load() throws Exception {
    node = loader.load();
  }

  @Override
  public void save() throws Exception {
    loader.save(node);
  }

  @Override
  public Set<String> getWhitelist() {
    return this.get("whitelist", WHITELIST_TYPE, this.defaultWhitelist);
  }

  @Override
  public void setWhitelist(Set<String> whitelist) {
    this.set("whitelist", WHITELIST_TYPE, whitelist);
  }

  @Override
  public Set<String> getBlacklist() {
    return this.get("blacklist", BLACKLIST_TYPE, this.defaultBlacklist);
  }

  @Override
  public void setBlacklist(Set<String> blacklist) {
    this.set("blacklist", BLACKLIST_TYPE, blacklist);
  }

  @Override
  public Map<String, KeyPermissions> getKeys() {
    return this.get("keys", KEYS_TYPE, this.defaultKeys);
  }

  @Override
  public void setKeys(Map<String, KeyPermissions> keys) {
    this.set("keys", KEYS_TYPE, keys);
  }

  private <T> T get(String path, TypeToken<T> type, T def) {
    try {
      CommentedConfigurationNode n = node.node(path);
      return n.get(type, def);
    } catch (SerializationException e) {
      return def;
    }
  }

  private <T> void set(String path, TypeToken<T> type, T value) {
    try {
      CommentedConfigurationNode n = node.node(path);
      n.set(type, value);
    } catch (SerializationException ignored) {
    }
  }
}
