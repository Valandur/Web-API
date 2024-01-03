package io.valandur.webapi.forge.security;

import io.valandur.webapi.forge.config.ForgeConfig;
import io.valandur.webapi.security.KeyPermissions;
import io.valandur.webapi.security.SecurityConfig;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.*;

public class ForgeSecurityConfig extends ForgeConfig implements SecurityConfig {

    private final ForgeConfigSpec.ConfigValue<List<? extends String>> WHITELIST = BUILDER
            .comment("A list of IPs that are allowed to access the API")
            .defineListAllowEmpty("whitelist", defaultWhitelist, o -> true);

    private final ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKLIST = BUILDER
            .comment("A list of IPs that are banned from accessing the API")
            .defineListAllowEmpty("blacklist", defaultBlacklist, o -> true);

    public ForgeSecurityConfig() {
        super("security.toml");

        build();
    }

    @Override
    public List<String> getWhitelist() {
        return new ArrayList<>(WHITELIST.get());
    }

    @Override
    public void setWhitelist(List<String> whitelist) {
        WHITELIST.set(new ArrayList<>(whitelist));
    }

    @Override
    public List<String> getBlacklist() {
        return new ArrayList<>(BLACKLIST.get());
    }

    @Override
    public void setBlacklist(List<String> blacklist) {
        BLACKLIST.set(new ArrayList<>(blacklist));
    }

    @Override
    public Map<String, KeyPermissions> getKeys() {
        return defaultKeys;
    }

    @Override
    public void setKeys(Map<String, KeyPermissions> keys) {

    }
}
