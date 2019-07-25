package valandur.webapi;

import org.slf4j.Logger;
import valandur.webapi.config.IMainConfig;
import valandur.webapi.config.IServletsConfig;
import valandur.webapi.servlet.base.ServletService;

import java.util.Optional;

public interface IPlugin {
    Logger getLogger();

    IMainConfig getMainConfig();
    IServletsConfig getServletsConfig();

    Optional<String> getAssetLocation(String asset);
    Optional<byte[]> getAssetContent(String asset);

    ServletService getServletService();

    void captureException(Throwable e);
}
