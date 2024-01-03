package io.valandur.webapi.security;

import io.valandur.webapi.Config;

import java.util.*;

public interface SecurityConfig extends Config {

  List<String> defaultWhitelist = new ArrayList<>();

  List<String> getWhitelist();

  void setWhitelist(List<String> whitelist);

  List<String> defaultBlacklist = new ArrayList<>();

  List<String> getBlacklist();

  void setBlacklist(List<String> blacklist);

  Map<String, KeyPermissions> defaultKeys = new HashMap<>();

  Map<String, KeyPermissions> getKeys();

  void setKeys(Map<String, KeyPermissions> keys);
}
