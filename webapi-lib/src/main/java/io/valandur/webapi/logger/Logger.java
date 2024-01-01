package io.valandur.webapi.logger;

public abstract class Logger {

  public abstract void info(String message);

  public abstract void warn(String message);

  public abstract void error(String message);
}
