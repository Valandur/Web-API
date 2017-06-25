package valandur.webapi.servlet;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import valandur.webapi.annotation.WebAPISpec;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

public class RegistryServlet extends WebAPIServlet {

    @WebAPISpec(method = "GET", path = "/:class", perm = "registry.get")
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
