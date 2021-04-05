package io.valandur.webapi.config;

import io.leangen.geantyref.TypeToken;

public abstract class Config {

    protected String name;

    public String getName() {
        return name;
    }

    public Config(String name) {
        this.name = name;
    }

    public abstract void load();

    public abstract void save();

    public abstract <T> T get(String path, TypeToken<T> type, T def);

    public abstract <T> boolean set(String path, TypeToken<T> type, T value);
}
