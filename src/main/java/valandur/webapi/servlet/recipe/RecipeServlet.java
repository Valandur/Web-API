package valandur.webapi.servlet.recipe;

import valandur.webapi.api.annotation.WebAPIRoute;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.servlet.IServlet;
import valandur.webapi.servlet.ServletData;

import javax.servlet.http.HttpServletResponse;

@WebAPIServlet(basePath = "recipe")
public class RecipeServlet implements IServlet {

    @WebAPIRoute(method = "GET", path = "/", perm = "list")
    public void get(ServletData data) {
        data.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Recipes have not yet been implemented in Sponge");
    }
}
