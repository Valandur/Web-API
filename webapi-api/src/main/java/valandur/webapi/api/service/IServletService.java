package valandur.webapi.api.service;

import valandur.webapi.api.servlet.IServlet;

public interface IServletService {
    public void registerServlet(Class<? extends IServlet> servlet);
}
