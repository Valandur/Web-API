package valandur.webapi.servlets;

import org.eclipse.jetty.http.HttpStatus;
import valandur.webapi.misc.Permission;

import javax.servlet.http.HttpServletResponse;

public class RecipeServlet extends WebAPIServlet {
    @Override
    @Permission(perm = "recipe")
    protected void handleGet(ServletData data) {
        data.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
        data.addJson("error", "Recipes have not yet been implemented in Sponge");
    }
}
