package valandur.webapi.servlet.registry;

import org.eclipse.jetty.http.HttpMethod;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import valandur.webapi.api.annotation.WebAPIEndpoint;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.servlet.WebAPIBaseServlet;
import valandur.webapi.servlet.ServletData;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

@WebAPIServlet(basePath = "registry")
public class RegistryServlet extends WebAPIBaseServlet {

    @WebAPIEndpoint(method = HttpMethod.GET, path = "/:class", perm = "one")
    public void getRegistry(ServletData data, String className) {
        try {
            Class type = Class.forName(className);
            if (!CatalogType.class.isAssignableFrom(type)) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Class must be a CatalogType");
                return;
            }

            Collection<CatalogType> types = Sponge.getRegistry().getAllOf(type);
            data.addJson("types", types, false);
        } catch (ClassNotFoundException e) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Class " + className + " could not be found");
        }
    }
}
