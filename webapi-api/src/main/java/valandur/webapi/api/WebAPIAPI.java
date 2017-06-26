package valandur.webapi.api;

import org.spongepowered.api.Sponge;
import valandur.webapi.api.service.IServletService;

import java.util.Optional;

public class WebAPIAPI {
    public static Optional<IServletService> getServletService() {
        return Sponge.getServiceManager().provide(IServletService.class);
    }
}
