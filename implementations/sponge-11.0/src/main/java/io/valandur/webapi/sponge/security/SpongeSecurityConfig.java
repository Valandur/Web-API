package io.valandur.webapi.sponge.security;

import io.leangen.geantyref.TypeToken;
import io.valandur.webapi.security.SecurityConfig;
import io.valandur.webapi.security.KeyPermissions;
import io.valandur.webapi.sponge.SpongeWebAPIPlugin;
import io.valandur.webapi.sponge.config.SpongeConfig;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SpongeSecurityConfig extends SpongeConfig implements SecurityConfig {

    private static final TypeToken<List<String>> WHITELIST_TYPE = new TypeToken<>() {
    };
    private static final TypeToken<List<String>> BLACKLIST_TYPE = new TypeToken<>() {
    };
    private static final TypeToken<Map<String, KeyPermissions>> KEYS_TYPE = new TypeToken<>() {
    };

    public SpongeSecurityConfig(SpongeWebAPIPlugin plugin) {
        super(plugin, "security.conf", Collections.singletonMap(TypeToken.get(KeyPermissions.class), new KeyPermissionsSerializer()));
    }

    @Override
    public List<String> getWhitelist() {
        return get("whitelist", WHITELIST_TYPE, defaultWhitelist);
    }

    @Override
    public void setWhitelist(List<String> whitelist) {
        set("whitelist", WHITELIST_TYPE, whitelist);
    }

    @Override
    public List<String> getBlacklist() {
        return get("blacklist", BLACKLIST_TYPE, defaultBlacklist);
    }

    @Override
    public void setBlacklist(List<String> blacklist) {
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