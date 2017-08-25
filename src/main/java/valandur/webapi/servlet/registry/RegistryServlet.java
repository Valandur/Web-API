package valandur.webapi.servlet.registry;

import org.eclipse.jetty.http.HttpMethod;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import valandur.webapi.WebAPI;
import valandur.webapi.api.annotation.WebAPIEndpoint;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.servlet.WebAPIBaseServlet;
import valandur.webapi.servlet.ServletData;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@WebAPIServlet(basePath = "registry")
public class RegistryServlet extends WebAPIBaseServlet {

    private Map<Class<? extends CatalogType>, Collection<? extends CatalogType>> registryCache = new ConcurrentHashMap<>();

    @WebAPIEndpoint(method = HttpMethod.GET, path = "/:class", perm = "one")
    public void getRegistry(ServletData data, String className) {
        try {
            Class rawType = Class.forName(className);
            if (!CatalogType.class.isAssignableFrom(rawType)) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Class must be a CatalogType");
                return;
            }
            Class<? extends CatalogType> type = rawType;

            if (registryCache.containsKey(type)) {
                data.addJson("ok", true, false);
                data.addJson("types", registryCache.get(type), false);
                return;
            }

            Optional<Collection<? extends CatalogType>> optTypes = WebAPI.runOnMain(() -> Sponge.getRegistry().getAllOf(type));
            optTypes.map(types -> registryCache.put(type, types));

            data.addJson("ok", optTypes.isPresent(), false);
            data.addJson("types", optTypes.orElse(null), false);
        } catch (ClassNotFoundException e) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Class " + className + " could not be found");
        }
    }
}
