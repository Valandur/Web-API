package io.valandur.webapi.fabric.config;

import io.valandur.webapi.security.SecurityConfig;
import io.valandur.webapi.security.KeyPermissions;

import java.util.List;
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
  public List<String> getWhitelist() {
    return defaultWhitelist;
  }

  @Override
  public void setWhitelist(List<String> whitelist) {

  }

  @Override
  public List<String> getBlacklist() {
    return defaultBlacklist;
  }

  @Override
  public void setBlacklist(List<String> blacklist) {

  }

  @Override
  public Map<String, KeyPermissions> getKeys() {
    return defaultKeys;
  }

  @Override
  public void setKeys(Map<String, KeyPermissions> keys) {

  }
}
