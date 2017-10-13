package valandur.webapi.servlet;

import org.eclipse.jetty.http.HttpMethod;
import valandur.webapi.api.servlet.Endpoint;
import valandur.webapi.api.servlet.Servlet;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.servlet.base.ServletData;

import javax.servlet.http.HttpServletResponse;

@Servlet(basePath = "recipe")
public class RecipeServlet extends BaseServlet {

    @Endpoint(method = HttpMethod.GET, path = "/", perm = "list")
    public void get(ServletData data) {
        data.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Recipes have not yet been implemented in Sponge");
    }
}
