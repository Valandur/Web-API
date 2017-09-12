package valandur.webapi.servlet;

import org.eclipse.jetty.http.HttpMethod;
import valandur.webapi.api.annotation.WebAPIEndpoint;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.servlet.WebAPIBaseServlet;
import valandur.webapi.servlet.base.ServletData;

import java.util.Map;

@WebAPIServlet(basePath = "servlet")
public class ServletServlet extends WebAPIBaseServlet {

    @WebAPIEndpoint(method = HttpMethod.GET, path = "/", perm = "list")
    public void getServlets(ServletData data) {
        Map<String, Class<? extends WebAPIBaseServlet>> servlets = servletService.getLoadedServlets();

        data.addJson("ok", true, false);
        data.addJson("servlets", servlets, false);
    }
}
