package valandur.webapi.api.servlet;

import java.util.Map;

/**
 * This service allows registering servlets with the Web-API, which it will serve for clients.
 * Your servlet must inherit from {@link BaseServlet} and have the
 * {@link Servlet} annotation specifying the base path at which the servlet will
 * be accessible.
 */
public interface IServletService {

    /**
     * Register a servlet with the WebAPI, which will give it a separate base address
     * @param servlet The class of servlet to register. The WebAPI will create an instance when starting. Make
     *                sure to provide an empty constructor.
     */
    void registerServlet(Class<? extends BaseServlet> servlet);

    /**
     * Gets a map of all available base paths mapped to the servlets that implement them.
     * @return A map from base path to implementing servlet class.
     */
    Map<String, Class<? extends BaseServlet>> getLoadedServlets();
}
