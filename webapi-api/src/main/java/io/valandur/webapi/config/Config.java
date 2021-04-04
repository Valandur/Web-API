package io.valandur.webapi.config;

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

    public abstract <T> T get(String path, T def);
}
