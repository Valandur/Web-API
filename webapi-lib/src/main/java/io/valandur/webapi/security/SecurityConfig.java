package io.valandur.webapi.security;

import io.valandur.webapi.Config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface SecurityConfig extends Config {

  Set<String> defaultWhitelist = new HashSet<>();

  Set<String> getWhitelist();

  void setWhitelist(Set<String> whitelist);

  Set<String> defaultBlacklist = new HashSet<>();

  Set<String> getBlacklist();

  void setBlacklist(Set<String> blacklist);

  Map<String, KeyPermissions> defaultKeys = new HashMap<>();

  Map<String, KeyPermissions> getKeys();

  void setKeys(Map<String, KeyPermissions> keys);
}
