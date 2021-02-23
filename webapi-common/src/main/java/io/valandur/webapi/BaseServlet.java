package io.valandur.webapi;

public abstract class BaseServlet {

    protected WebAPI<?> webapi;

    public BaseServlet() {
        this.webapi = WebAPI.instance;
    }
}
