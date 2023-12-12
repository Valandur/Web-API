package io.valandur.webapi.sponge.config;

import io.leangen.geantyref.TypeToken;
import io.valandur.webapi.config.SecurityConfig;
import io.valandur.webapi.config.ServerConfig;
import io.valandur.webapi.security.KeyPermissions;
import io.valandur.webapi.sponge.SpongeWebAPIPlugin;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.spongepowered.api.Sponge;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

public class SpongeServerConfig extends ServerConfig {

  private final ConfigurationLoader<CommentedConfigurationNode> loader;
  private CommentedConfigurationNode node;

  public SpongeServerConfig(SpongeWebAPIPlugin plugin) {
    var serializers = Sponge.configManager().serializers().childBuilder()
        .register(TypeToken.get(KeyPermissions.class), new KeyPermissionsSerializer())
        .build();

    var path = plugin.getConfigPath().resolve("server.conf");
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
  public String getBasePath() {
    return this.get("path", TypeToken.get(String.class), this.defaultBasePath);
  }

  @Override
  public String getHost() {
    return this.get("host", TypeToken.get(String.class), this.defaultHost);
  }

  @Override
  public int getPort() {
    return this.get("port", TypeToken.get(Integer.class), this.defaultPort);
  }

  @Override
  public int getMinThreads() {
    return this.get("minThreads", TypeToken.get(Integer.class), this.defaultMinThreads);
  }

  @Override
  public int getMaxThreads() {
    return this.get("maxThreads", TypeToken.get(Integer.class), this.defaultMaxThreads);
  }

  @Override
  public int getIdleTimeout() {
    return this.get("idleTimeout", TypeToken.get(Integer.class), this.defaultIdleTimeout);
  }

  private <T> T get(String path, TypeToken<T> type, T def) {
    try {
      CommentedConfigurationNode n = node.node(path);
      return (T) n.get(type, def);
    } catch (SerializationException e) {
      return def;
    }
  }
}
