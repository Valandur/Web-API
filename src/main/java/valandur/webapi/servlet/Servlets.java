package valandur.webapi.servlet;

import org.slf4j.Logger;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.WebAPI;
import valandur.webapi.api.annotation.WebAPIRoute;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.servlet.IServlet;
import valandur.webapi.misc.Util;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class Servlets {
    private static Map<String, Class<? extends IServlet>> servletClasses = new HashMap<>();
    private static Map<String, IServlet> servlets = new HashMap<>();
    private static Map<IServlet, List<Tuple<WebAPIRoute, Method>>> servletMethods = new HashMap<>();

    public static void init() {
        Logger logger = WebAPI.getInstance().getLogger();

        servlets.clear();

        logger.info("Initializing servlets...");
        for (Class<? extends IServlet> servletClass : servletClasses.values()) {
            String basePath = servletClass.getAnnotation(WebAPIServlet.class).basePath();
            logger.info("  " + basePath + " -> " + servletClass.getName());

            if (basePath.contains("/")) {
                logger.error("  Base path is not allowed to have slashes");
                continue;
            }

            // Create a new instance
            try {
                IServlet serv = servletClass.newInstance();
                servlets.put(basePath, serv);

                // Get all methods that are correctly annotated
                List<Tuple<WebAPIRoute, Method>> newMethods = Arrays.stream(servletClass.getMethods())
                        .filter(m -> m.isAnnotationPresent(WebAPIRoute.class))
                        .map(m -> new Tuple<>(m.getAnnotation(WebAPIRoute.class), m))
                        .collect(Collectors.toList());

                for (Tuple<WebAPIRoute, Method> tuple : newMethods) {
                    WebAPIRoute route = tuple.getFirst();
                    Method method = tuple.getSecond();
                    method.setAccessible(true);
                    logger.debug("    [" + route.method() + "] " + route.path() + " -> " +method.getName());
                }
                servletMethods.put(serv, newMethods);
            } catch (InstantiationException | IllegalAccessException e) {
                logger.error("  -> Could not init servlet " + servletClass.getName() + ": " + e.getMessage());
            }
        }
    }

    public static Optional<MatchedRoute> getMethod(String verb, String path) {
        // First get the correct servlet
        List<String> pathParts = Util.getPathParts(path);

        IServlet servlet = servlets.get(pathParts.get(0));
        if (servlet == null) {
            return Optional.empty();
        }

        // Then get the methods for that servlet
        List<Tuple<WebAPIRoute, Method>> methods = servletMethods.get(servlet);

        // Find the most suitable method according to the path
        Map<String, String> bestMatches = null;
        Tuple<WebAPIRoute, Method> bestTuple = null;

        pathParts = pathParts.subList(1, pathParts.size());
        for (Tuple<WebAPIRoute, Method> tuple : methods) {
            WebAPIRoute spec = tuple.getFirst();
            List<String> specPathParts = Util.getPathParts(spec.path());

            // Skip methods that don't match the verb or route
            if (!spec.method().equalsIgnoreCase(verb) || specPathParts.size() != pathParts.size())
                continue;

            boolean doesMatch = true;
            Map<String, String> matches = new HashMap<>();
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

    public static void registerServlet(Class<? extends IServlet> servlet) {
        Logger logger = WebAPI.getInstance().getLogger();

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
