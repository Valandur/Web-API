package valandur.webapi.servlet;

import org.eclipse.jetty.http.HttpMethod;
import valandur.webapi.api.servlet.Endpoint;
import valandur.webapi.api.servlet.Servlet;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.servlet.base.ServletData;

import java.util.Map;

@Servlet(basePath = "servlet")
public class ServletServlet extends BaseServlet {

    @Endpoint(method = HttpMethod.GET, path = "/", perm = "list")
    public void getServlets(ServletData data) {
        Map<String, Class<? extends BaseServlet>> servlets = servletService.getLoadedServlets();

        data.addData("ok", true, false);
        data.addData("servlets", servlets, false);
    }
}
