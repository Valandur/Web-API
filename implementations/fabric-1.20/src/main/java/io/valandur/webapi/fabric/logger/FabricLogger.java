package io.valandur.webapi.fabric.logger;

import io.valandur.webapi.logger.Logger;
import org.apache.logging.log4j.LogManager;

public class FabricLogger extends Logger {

  private final org.apache.logging.log4j.Logger logger = LogManager.getLogger("WebAPI");

  @Override
  public void info(String message) {
    logger.info(message);
  }

  @Override
  public void warn(String message) {
    logger.warn(message);
  }

  @Override
  public void error(String message) {
    logger.error(message);
  }
}
