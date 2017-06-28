package valandur.webapi.api.service;

import valandur.webapi.api.servlet.WebAPIBaseServlet;

public interface IServletService {

    /**
     * Register a servlet with the WebAPI, which will give it a separate base address
     * @param servlet The class of servlet to register. The WebAPI will create an instance when starting. Make
     *                sure to provide an empty constructor.
     */
    void registerServlet(Class<? extends WebAPIBaseServlet> servlet);
}
