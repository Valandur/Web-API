package valandur.webapi.servlet.recipe;

import org.eclipse.jetty.http.HttpMethod;
import valandur.webapi.api.annotation.WebAPIEndpoint;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.servlet.WebAPIBaseServlet;
import valandur.webapi.servlet.ServletData;

import javax.servlet.http.HttpServletResponse;

@WebAPIServlet(basePath = "recipe")
public class RecipeServlet extends WebAPIBaseServlet {

    @WebAPIEndpoint(method = HttpMethod.GET, path = "/", perm = "list")
    public void get(ServletData data) {
        data.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Recipes have not yet been implemented in Sponge");
    }
}
