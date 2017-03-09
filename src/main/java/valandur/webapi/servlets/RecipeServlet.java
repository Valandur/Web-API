package valandur.webapi.servlets;

import org.eclipse.jetty.http.HttpStatus;
import valandur.webapi.Permission;

import javax.servlet.http.HttpServletResponse;

public class RecipeServlet extends WebAPIServlet {
    @Override
    @Permission(perm = "recipe")
    protected void handleGet(ServletData data) {
        data.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);

        data.addJson("code", HttpServletResponse.SC_NOT_IMPLEMENTED);
        data.addJson("message", HttpStatus.getMessage(HttpServletResponse.SC_NOT_IMPLEMENTED));
    }
}
