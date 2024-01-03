package io.valandur.webapi.forge.logger;

import com.mojang.logging.LogUtils;
import io.valandur.webapi.logger.Logger;

public class ForgeLogger extends Logger {

    private static final org.slf4j.Logger LOGGER = LogUtils.getLogger();

    @Override
    public void info(String message) {
        LOGGER.info(message);
    }

    @Override
    public void warn(String message) {
        LOGGER.warn(message);
    }

    @Override
    public void error(String message) {
        LOGGER.error(message);
    }
}
