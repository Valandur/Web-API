package io.valandur.webapi.logger;

public class ForgeLogger extends Logger {

    private final org.apache.logging.log4j.Logger logger;

    public ForgeLogger(org.apache.logging.log4j.Logger logger) {
        this.logger = logger;
    }

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
