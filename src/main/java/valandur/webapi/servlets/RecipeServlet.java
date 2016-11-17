package valandur.webapi.servlets;

import org.eclipse.jetty.http.HttpStatus;
import valandur.webapi.Permission;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class RecipeServlet extends APIServlet {
    @Override
    @Permission(perm = "recipe")
    protected Optional<CompletableFuture> handleGet(ServletData data) throws ServletException, IOException {
        data.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);

        data.getJson().addProperty("code", HttpServletResponse.SC_NOT_IMPLEMENTED);
        data.getJson().addProperty("message", HttpStatus.getMessage(HttpServletResponse.SC_NOT_IMPLEMENTED));

        return Optional.empty();
    }
}
