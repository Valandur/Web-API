package valandur.webapi.servlets;

import com.google.gson.JsonObject;
import org.eclipse.jetty.http.HttpStatus;
import valandur.webapi.Permission;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class RecipeServlet extends APIServlet {
    @Override
    @Permission(perm = "recipe")
    protected void handleGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject json = new JsonObject();
        resp.setContentType("application/json; charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);

        json.addProperty("code", HttpServletResponse.SC_NOT_IMPLEMENTED);
        json.addProperty("message", HttpStatus.getMessage(HttpServletResponse.SC_NOT_IMPLEMENTED));

        PrintWriter out = resp.getWriter();
        out.print(json);
    }
}
