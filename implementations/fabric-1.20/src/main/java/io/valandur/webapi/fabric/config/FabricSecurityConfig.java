package io.valandur.webapi.fabric.config;

import io.valandur.webapi.config.SecurityConfig;
import io.valandur.webapi.security.KeyPermissions;
import java.util.Map;
import java.util.Set;

public class FabricSecurityConfig implements SecurityConfig {

  @Override
  public void save() throws Exception {

  }

  @Override
  public void load() throws Exception {

  }

  @Override
  public Set<String> getWhitelist() {
    return defaultWhitelist;
  }

  @Override
  public void setWhitelist(Set<String> whitelist) {

  }

  @Override
  public Set<String> getBlacklist() {
    return defaultBlacklist;
  }

  @Override
  public void setBlacklist(Set<String> blacklist) {

  }

  @Override
  public Map<String, KeyPermissions> getKeys() {
    return defaultKeys;
  }

  @Override
  public void setKeys(Map<String, KeyPermissions> keys) {

  }
}
