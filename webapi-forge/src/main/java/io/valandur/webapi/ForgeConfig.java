package io.valandur.webapi;

import io.valandur.webapi.config.Config;

public class ForgeConfig extends Config {
    public ForgeConfig(String name) {
        super(name);
    }

    @Override
    public void load() {
    }

    @Override
    public void save() {

    }

    @Override
    public <T> T get(String path, T def) {
        return def;
    }
}
