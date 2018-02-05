package valandur.webapi.servlet;

import org.eclipse.jetty.http.HttpMethod;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import valandur.webapi.WebAPI;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Endpoint;
import valandur.webapi.api.servlet.Servlet;
import valandur.webapi.cache.misc.CachedCatalogType;
import valandur.webapi.servlet.base.ServletData;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Servlet(basePath = "registry")
public class RegistryServlet extends BaseServlet {

    private Map<Class<? extends CatalogType>, Collection<CachedCatalogType>> registryCache = new ConcurrentHashMap<>();

    @Endpoint(method = HttpMethod.GET, path = "/:class", perm = "one")
    public void getRegistry(ServletData data, String className) {
        try {
            Class rawType = Class.forName(className);
            if (!CatalogType.class.isAssignableFrom(rawType)) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Class must be a CatalogType");
                return;
            }
            Class<? extends CatalogType> type = rawType;

            if (registryCache.containsKey(type)) {
                data.addData("ok", true, false);
                data.addData("types", registryCache.get(type), false);
                return;
            }

            Optional<Collection<CachedCatalogType>> optTypes = WebAPI.runOnMain(() -> {
                Collection<CachedCatalogType> coll = Sponge.getRegistry().getAllOf(type).stream()
                        .map(t -> new CachedCatalogType(t))
                        .collect(Collectors.toList());
                registryCache.put(type, coll);
                return coll;
            });

            data.addData("ok", optTypes.isPresent(), false);
            data.addData("types", optTypes.orElse(null), false);
        } catch (ClassNotFoundException e) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Class " + className + " could not be found");
        }
    }
}
