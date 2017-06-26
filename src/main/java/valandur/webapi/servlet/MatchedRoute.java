package valandur.webapi.servlet;

import valandur.webapi.api.annotation.WebAPIRoute;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.servlet.IServlet;

import java.lang.reflect.Method;
import java.util.Map;

public class MatchedRoute {
    private WebAPIServlet servletSpec;
    public WebAPIServlet getServletSpec() {
        return servletSpec;
    }

    private IServlet servlet;
    public IServlet getServlet() {
        return servlet;
    }

    private WebAPIRoute route;
    public WebAPIRoute getRoute() {
        return route;
    }

    private Method method;
    public Method getMethod() {
        return method;
    }

    private Map<String, String> matchedParts;
    public Map<String, String> getMatchedParts() {
        return matchedParts;
    }


    public MatchedRoute(IServlet servlet, WebAPIRoute route,
                        Method method, Map<String, String> matchedParts) {
        this.servletSpec = servlet.getClass().getAnnotation(WebAPIServlet.class);
        this.servlet = servlet;
        this.route = route;
        this.method = method;
        this.matchedParts = matchedParts;
    }
}
