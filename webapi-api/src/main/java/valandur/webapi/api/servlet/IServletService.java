package valandur.webapi.api.servlet;

/**
 * This service allows registering servlets with the Web-API, which it will serve for clients.
 * Your servlet must inherit from {@link WebAPIBaseServlet} and have the
 * {@link valandur.webapi.api.annotation.WebAPIServlet} annotation specifying the base path at which the servlet will
 * be accessible.
 */
public interface IServletService {

    /**
     * Register a servlet with the WebAPI, which will give it a separate base address
     * @param servlet The class of servlet to register. The WebAPI will create an instance when starting. Make
     *                sure to provide an empty constructor.
     */
    void registerServlet(Class<? extends WebAPIBaseServlet> servlet);
}
