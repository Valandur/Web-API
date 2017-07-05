package valandur.webapi.servlet;

import org.slf4j.Logger;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.WebAPI;
import valandur.webapi.api.annotation.WebAPIEndpoint;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.servlet.IServletService;
import valandur.webapi.api.servlet.IServletData;
import valandur.webapi.api.servlet.WebAPIBaseServlet;
import valandur.webapi.util.Util;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ServletService implements IServletService {

    private Map<String, Class<? extends WebAPIBaseServlet>> servletClasses = new HashMap<>();
    private Map<String, WebAPIBaseServlet> servlets = new HashMap<>();
    private Map<WebAPIBaseServlet, List<Tuple<WebAPIEndpoint, Method>>> servletMethods = new HashMap<>();


    public void init() {
        Logger logger = WebAPI.getLogger();

        servlets.clear();

        logger.info("Initializing servlets...");
        for (Class<? extends WebAPIBaseServlet> servletClass : servletClasses.values()) {
            String basePath = servletClass.getAnnotation(WebAPIServlet.class).basePath();
            logger.info("  " + basePath + " -> " + servletClass.getName());

            if (basePath.contains("/")) {
                logger.error("  Base path is not allowed to have slashes");
                continue;
            }

            // Create a new instance
            try {
                WebAPIBaseServlet serv = servletClass.newInstance();
                servlets.put(basePath, serv);

                // Get all methods that are correctly annotated
                List<Tuple<WebAPIEndpoint, Method>> newMethods = Arrays.stream(servletClass.getMethods())
                        .filter(m -> m.isAnnotationPresent(WebAPIEndpoint.class))
                        .map(m -> new Tuple<>(m.getAnnotation(WebAPIEndpoint.class), m))
                        .collect(Collectors.toList());

                for (Tuple<WebAPIEndpoint, Method> tuple : newMethods) {
                    WebAPIEndpoint route = tuple.getFirst();
                    Method method = tuple.getSecond();
                    /*if (method.getParameterTypes().length != 1) {
                        logger.error("    Method " + method.getName() + " may only have 1 argument");
                        continue;
                    }*/
                    if (method.getParameterTypes()[0] != IServletData.class &&
                            method.getParameterTypes()[0] != ServletData.class) {
                        logger.error("    Method " + method.getName() + " first parameter is not of type IServletData");
                        continue;
                    }
                    method.setAccessible(true);
                    logger.debug("    [" + route.method() + "] " + route.path() + " -> " +method.getName());
                }
                servletMethods.put(serv, newMethods);
            } catch (InstantiationException | IllegalAccessException e) {
                logger.error("  -> Could not init servlet " + servletClass.getName() + ": " + e.getMessage());
            }
        }
    }

    public Optional<MatchedRoute> getMethod(String verb, String path) {
        // First get the correct servlet
        List<String> pathParts = Util.getPathParts(path);

        WebAPIBaseServlet servlet = servlets.get(pathParts.get(0));
        if (servlet == null) {
            return Optional.empty();
        }

        // Then get the methods for that servlet
        List<Tuple<WebAPIEndpoint, Method>> methods = servletMethods.get(servlet);

        // Find the most suitable method according to the path
        LinkedHashMap<String, String> bestMatches = null;
        Tuple<WebAPIEndpoint, Method> bestTuple = null;

        pathParts = pathParts.subList(1, pathParts.size());
        for (Tuple<WebAPIEndpoint, Method> tuple : methods) {
            WebAPIEndpoint spec = tuple.getFirst();
            List<String> specPathParts = Util.getPathParts(spec.path());

            // Skip methods that don't match the verb or route
            if (!spec.method().asString().equalsIgnoreCase(verb) || specPathParts.size() != pathParts.size())
                continue;

            boolean doesMatch = true;
            LinkedHashMap<String, String> matches = new LinkedHashMap<>();
            for (int i = 0; i < specPathParts.size(); i++) {
                if (!specPathParts.get(i).startsWith(":")) {
                    if (!specPathParts.get(i).equalsIgnoreCase(pathParts.get(i))) {
                        doesMatch = false;
                        break;
                    }
                } else {
                    String param = specPathParts.get(i).substring(1);
                    matches.put(param, pathParts.get(i));
                }
            }

            if (!doesMatch)
                continue;

            if (bestMatches == null || matches.size() < bestMatches.size()) {
                bestMatches = matches;
                bestTuple = tuple;
            }
        }

        if (bestTuple == null) {
            return Optional.empty();
        }

        return Optional.of(new MatchedRoute(servlet, bestTuple.getFirst(), bestTuple.getSecond(), bestMatches));
    }

    public void registerServlet(Class<? extends WebAPIBaseServlet> servlet) {
        Logger logger = WebAPI.getLogger();

        if (!servlet.isAnnotationPresent(WebAPIServlet.class)) {
            logger.error("Servlet " + servlet.getName() + " is missing @WebAPIServlet annotation");
            return;
        }

        WebAPIServlet info = servlet.getAnnotation(WebAPIServlet.class);
        String basePath = info.basePath();
        if (basePath.endsWith("/"))
            basePath = basePath.substring(0, basePath.length() - 1);
        if (!basePath.startsWith("/"))
            basePath = "/" + basePath;

        if (servlets.containsKey(info.basePath())) {
            logger.error("Servlet " + servlet.getName() + " is trying to register base path " + basePath
                    + " which is already registered to " + servlets.get(info.basePath()));
            return;
        }

        servletClasses.put(info.basePath(), servlet);
    }
}
