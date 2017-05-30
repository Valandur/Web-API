package valandur.webapi.servlet;

import valandur.webapi.permission.Permission;

import javax.servlet.http.HttpServletResponse;

public class RecipeServlet extends WebAPIServlet {
    @Override
    @Permission(perm = "recipe.get")
    protected void handleGet(ServletData data) {
        data.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Recipes have not yet been implemented in Sponge");
    }
}
