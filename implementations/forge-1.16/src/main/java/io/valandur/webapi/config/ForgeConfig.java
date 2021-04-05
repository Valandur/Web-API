package io.valandur.webapi.config;

import io.leangen.geantyref.TypeToken;

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
    public <T> T get(String path, TypeToken<T> type, T def) {
        return def;
    }

    @Override
    public <T> boolean set(String path, TypeToken<T> type, T value) {
        return false;
    }
}
