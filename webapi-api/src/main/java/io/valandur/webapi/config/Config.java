package io.valandur.webapi.config;

public interface Config {
  void save() throws Exception;

  void load() throws Exception;
}
