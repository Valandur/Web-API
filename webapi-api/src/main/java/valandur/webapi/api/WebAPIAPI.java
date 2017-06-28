package valandur.webapi.api;

import org.spongepowered.api.Sponge;
import valandur.webapi.api.service.*;

import java.util.Optional;

public class WebAPIAPI {

    public static Optional<IBlockService> getBlockService() {
        return Sponge.getServiceManager().provide(IBlockService.class);
    }

    public static Optional<ICacheService> getCacheService() {
        return Sponge.getServiceManager().provide(ICacheService.class);
    }

    public static Optional<IExtensionService> getExtensionService() {
        return Sponge.getServiceManager().provide(IExtensionService.class);
    }

    public static Optional<IJsonService> getJsonService() {
        return Sponge.getServiceManager().provide(IJsonService.class);
    }

    public static Optional<IMessageService> getMessageService() {
        return Sponge.getServiceManager().provide(IMessageService.class);
    }

    public static Optional<IServerService> getServerService() {
        return Sponge.getServiceManager().provide(IServerService.class);
    }

    public static Optional<IServletService> getServletService() {
        return Sponge.getServiceManager().provide(IServletService.class);
    }

    public static Optional<IWebHookService> getWebHookService() {
        return Sponge.getServiceManager().provide(IWebHookService.class);
    }
}
