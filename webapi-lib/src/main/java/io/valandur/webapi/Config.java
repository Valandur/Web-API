package io.valandur.webapi;

public interface Config {
  String name = null;

  void save() throws Exception;

  void load() throws Exception;
}
