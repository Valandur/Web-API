package valandur.webapi.servlet.registry;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import valandur.webapi.api.annotation.WebAPIRoute;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.servlet.IServlet;
import valandur.webapi.servlet.ServletData;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

@WebAPIServlet(basePath = "registry")
public class RegistryServlet implements IServlet {

    @WebAPIRoute(method = "GET", path = "/:class", perm = "one")
    public void getRegistry(ServletData data) {
        String className = data.getPathParam("class");

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
