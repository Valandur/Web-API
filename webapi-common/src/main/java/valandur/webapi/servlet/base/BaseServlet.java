package valandur.webapi.servlet.base;

public abstract class BaseServlet {
    protected ServletService servletService;

    public BaseServlet() {
        servletService = WebAPI.getServletService();
    }
}
