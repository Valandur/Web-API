package io.valandur.webapi.sponge.config;

import io.leangen.geantyref.TypeToken;
import io.valandur.webapi.config.SecurityConfig;
import io.valandur.webapi.security.KeyPermissions;
import io.valandur.webapi.sponge.SpongeWebAPIPlugin;
import java.util.Map;
import java.util.Set;

public class SpongeSecurityConfig extends SpongeConfig implements SecurityConfig {

  private static final TypeToken<Set<String>> WHITELIST_TYPE = new TypeToken<>() {
  };
  private static final TypeToken<Set<String>> BLACKLIST_TYPE = new TypeToken<>() {
  };
  private static final TypeToken<Map<String, KeyPermissions>> KEYS_TYPE = new TypeToken<>() {
  };

  public SpongeSecurityConfig(SpongeWebAPIPlugin plugin) {
    super(plugin, "security.conf");
  }

  @Override
  public Set<String> getWhitelist() {
    return get("whitelist", WHITELIST_TYPE, defaultWhitelist);
  }

  @Override
  public void setWhitelist(Set<String> whitelist) {
    set("whitelist", WHITELIST_TYPE, whitelist);
  }

  @Override
  public Set<String> getBlacklist() {
    return get("blacklist", BLACKLIST_TYPE, defaultBlacklist);
  }

  @Override
  public void setBlacklist(Set<String> blacklist) {
    set("blacklist", BLACKLIST_TYPE, blacklist);
  }

  @Override
  public Map<String, KeyPermissions> getKeys() {
    return get("keys", KEYS_TYPE, defaultKeys);
  }

  @Override
  public void setKeys(Map<String, KeyPermissions> keys) {
    set("keys", KEYS_TYPE, keys);
  }
}
