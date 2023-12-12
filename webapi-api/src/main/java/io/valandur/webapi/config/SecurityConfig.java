package io.valandur.webapi.config;

import io.valandur.webapi.security.KeyPermissions;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class SecurityConfig implements Config {

  protected Set<String> defaultWhitelist = new HashSet<>();

  public abstract Set<String> getWhitelist();
  public abstract void setWhitelist(Set<String> whitelist);

  protected Set<String> defaultBlacklist = new HashSet<>();

  public abstract Set<String> getBlacklist();
  public abstract void setBlacklist(Set<String> blacklist);

  protected Map<String, KeyPermissions> defaultKeys = new HashMap<>();

  public abstract Map<String, KeyPermissions> getKeys();
  public abstract void setKeys(Map<String, KeyPermissions> keys);
}
