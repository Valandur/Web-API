package io.valandur.webapi.security;

import io.leangen.geantyref.TypeToken;
import io.valandur.webapi.WebAPI;
import io.valandur.webapi.config.Config;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SecurityService {

    public static final String API_KEY = "X-WebAPI-Key";
    public static final String X_FORWARDED_FOR = "X-Forwarded-For";
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";

    public static final String ACCESS_CONTROL_ORIGIN = "*";
    public static final String ACCESS_CONTROL_METHODS = "GET,PUT,POST,DELETE,OPTIONS";
    public static final String ACCESS_CONTROL_HEADERS = "origin, content-type, x-webapi-key, x-forwarded-for";

    private static final TypeToken<Set<String>> WHITELIST_TYPE = new TypeToken<>() {
    };
    private static final TypeToken<Set<String>> BLACKLIST_TYPE = new TypeToken<>() {
    };
    private static final TypeToken<Map<String, KeyPermissions>> KEYS_TYPE = new TypeToken<>() {
    };

    private final Config conf;
    private final Set<String> whitelist;
    private final Set<String> blacklist;
    private final Map<String, KeyPermissions> keys;

    private final Map<String, Double> lastCall = new ConcurrentHashMap<>();

    public SecurityService(WebAPI<?, ?> webapi) {
        this.conf = webapi.getConfig("security");
        this.conf.load();

        this.whitelist = conf.get("whitelist", WHITELIST_TYPE, new HashSet<>());
        this.blacklist = conf.get("blacklist", BLACKLIST_TYPE, new HashSet<>());
        this.keys = conf.get("keys", KEYS_TYPE, new HashMap<>());

        if (this.keys.size() == 0) {
            var key = this.generateKey();
            this.keys.put(key, new KeyPermissions(0, Access.WRITE));

            this.conf.set("keys", KEYS_TYPE, this.keys);
            this.conf.save();

            webapi.getLogger().info("Generated access key: " + key);
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
        return this.whitelist.size() == 0 || this.whitelist.contains(address);
    }

    public boolean blacklistContains(String address) {
        return this.blacklist.contains(address);
    }

    public KeyPermissions getPerms(String key) {
        return this.keys.get(key);
    }

    public boolean isRateLimited(String key, KeyPermissions perms) {
        if (perms.rateLimit <= 0) {
            return false;
        }

        double time = System.nanoTime() / 1000000000d;

        if (lastCall.containsKey(key) && time - lastCall.get(key) < 1d / perms.rateLimit) {
            return true;
        }

        lastCall.put(key, time);
        return false;
    }

    public boolean containsProxyIP(String ip) {
        return false;
    }
}
