package valandur.webapi.servlet;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import valandur.webapi.permission.Permission;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

public class RegistryServlet extends WebAPIServlet {

    @Override
    @Permission(perm = "registry.get")
    public void handleGet(ServletData data) {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            data.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Invalid method");
            return;
        }

        try {
            Class type = Class.forName(paths[0]);
            if (!CatalogType.class.isAssignableFrom(type)) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Class must be a CatalogType");
                return;
            }

            Collection<CatalogType> types = Sponge.getRegistry().getAllOf(type);
            data.addJson("types", types, false);
        } catch (ClassNotFoundException e) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Class " + paths[0] + " could not be found");
        }
    }
}
