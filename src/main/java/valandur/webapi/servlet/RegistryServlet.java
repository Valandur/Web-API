package valandur.webapi.servlet;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.misc.CachedCatalogType;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Permission;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Path("registry")
@Api(tags = { "Registry" }, value = "Query Sponge registry values, such as DimensionTypes and EntityTypes.")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class RegistryServlet extends BaseServlet {

    private Map<Class<? extends CatalogType>, Collection<CachedCatalogType>> registryCache = new ConcurrentHashMap<>();

    @GET
    @Path("/{class}")
    @Permission("one")
    @ApiOperation(value = "Get a catalog type", notes = "Lists all the catalog values of a specified CatalogType.")
    public Collection<CachedCatalogType> getRegistry(
            @PathParam("class") @ApiParam("The fully qualified classname of the catalog type") String className)
            throws BadRequestException, NotFoundException {
        try {
            Class rawType = Class.forName(className);
            if (!CatalogType.class.isAssignableFrom(rawType)) {
                throw new BadRequestException("Class must be a CatalogType");
            }
            Class<? extends CatalogType> type = rawType;

            if (registryCache.containsKey(type)) {
                return registryCache.get(type);
            }

            return WebAPI.runOnMain(() -> {
                Collection<CachedCatalogType> coll = Sponge.getRegistry().getAllOf(type).stream()
                        .map(t -> new CachedCatalogType<>(t))
                        .collect(Collectors.toList());
                registryCache.put(type, coll);
                return coll;
            });
        } catch (ClassNotFoundException e) {
            throw new NotFoundException("Class " + className + " could not be found");
        }
    }
}
