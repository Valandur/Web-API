package io.valandur.webapi.security;

import io.valandur.webapi.Service;
import io.valandur.webapi.WebAPI;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SecurityService<T extends WebAPI<?, ?>> extends Service<T> {

  public static final String API_KEY = "X-WebAPI-Key";
  public static final String X_FORWARDED_FOR = "X-Forwarded-For";
  public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
  public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
  public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";

  public static final String ACCESS_CONTROL_ORIGIN = "*";
  public static final String ACCESS_CONTROL_METHODS = "GET,PUT,POST,DELETE,OPTIONS";
  public static final String ACCESS_CONTROL_HEADERS = "origin, content-type, x-webapi-key, x-forwarded-for";

  private List<String> whitelist;
  private List<String> blacklist;
  private Map<String, KeyPermissions> keys;

  private final Map<String, Double> lastCall = new ConcurrentHashMap<>();

  public SecurityService(T webapi) {
    super(webapi);
  }

  @Override
  public void init() {
    super.init();

    var config = webapi.getSecurityConfig();
    try {
      config.load();
    } catch (Exception e) {
      logger.error("Could not load config: " + e.getMessage());
    }

    this.whitelist = config.getWhitelist();
    this.blacklist = config.getBlacklist();
    this.keys = config.getKeys();

    if (this.keys.isEmpty()) {
      var key = this.generateKey();
      var perms = new KeyPermissions(0, Access.WRITE);
      logger.info("Generated access key: " + key);

      this.keys.put(key, perms);
      config.setKeys(this.keys);
    }

    try {
      config.save();
    } catch (Exception e) {
      logger.error("Could not save config: " + e.getMessage());
    }
  }

  private String generateKey() {
    SecureRandom random = new SecureRandom();
    byte[] bytes = new byte[16];
    random.nextBytes(bytes);
    Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    return encoder.encodeToString(bytes);
  }

  public boolean whitelistContains(String address) {
    return this.whitelist.isEmpty() || this.whitelist.contains(address);
  }

  public boolean blacklistContains(String address) {
    return this.blacklist.contains(address);
  }

  public KeyPermissions getPerms(String key) {
    return this.keys.get(key);
  }

  public boolean isRateLimited(String key, KeyPermissions perms) {
    if (perms.rateLimit() <= 0) {
      return false;
    }

    double time = System.nanoTime() / 1000000000d;

    if (lastCall.containsKey(key) && time - lastCall.get(key) < 1d / perms.rateLimit()) {
      return true;
    }

    lastCall.put(key, time);
    return false;
  }

  public boolean containsProxyIP(String ip) {
    return false;
  }
}
