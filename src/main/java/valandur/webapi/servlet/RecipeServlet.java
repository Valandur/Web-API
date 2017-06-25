package valandur.webapi.servlet;

import valandur.webapi.annotation.WebAPISpec;

import javax.servlet.http.HttpServletResponse;

public class RecipeServlet extends WebAPIServlet {

    @WebAPISpec(method = "GET", path = "/", perm = "recipe.get")
    public void get(ServletData data) {
        data.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Recipes have not yet been implemented in Sponge");
    }
}
