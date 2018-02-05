package valandur.webapi.servlet.base;

import org.slf4j.Logger;
import valandur.webapi.WebAPI;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.IServletService;

import javax.ws.rs.Path;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ServletService implements IServletService {

    private Map<String, Class<? extends BaseServlet>> servletClasses = new HashMap<>();


    public void init() {
        Logger logger = WebAPI.getLogger();

        logger.info("Initializing servlets...");
        servletClasses.values().forEach(this::initServlet);
    }
    private void initServlet(Class<? extends BaseServlet> servletClass) {
        Logger logger = WebAPI.getLogger();

        String basePath = servletClass.getAnnotation(Path.class).value();
        logger.info("  " + basePath + " -> " + servletClass.getName());
    }

    public void registerServlet(Class<? extends BaseServlet> servlet) {
        Logger logger = WebAPI.getLogger();

        if (!servlet.isAnnotationPresent(Path.class)) {
            logger.error("Servlet " + servlet.getName() + " is missing @Path annotation");
            return;
        }

        Path info = servlet.getAnnotation(Path.class);
        String basePath = info.value();
        if (basePath.endsWith("/"))
            basePath = basePath.substring(0, basePath.length() - 1);
        if (!basePath.startsWith("/"))
            basePath = "/" + basePath;

        try {
            Method m = servlet.getMethod("onRegister");
            m.invoke(null);
        } catch (NoSuchMethodException ignored) {
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            if (WebAPI.reportErrors()) WebAPI.sentryCapture(e);
        }

        servletClasses.put(basePath, servlet);
    }

    @Override
    public Map<String, Class<? extends BaseServlet>> getRegisteredServlets() {
        return servletClasses;
    }
}
