package valandur.webapi.servlet.base;

import org.eclipse.jetty.http.HttpMethod;
import org.slf4j.Logger;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.WebAPI;
import valandur.webapi.api.servlet.*;
import valandur.webapi.util.Util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ServletService implements IServletService {

    private Map<String, Class<? extends BaseServlet>> servletClasses = new HashMap<>();
    private Map<String, BaseServlet> servlets = new HashMap<>();
    private Map<BaseServlet, List<Tuple<Endpoint, Method>>> servletMethods = new HashMap<>();


    public void init() {
        Logger logger = WebAPI.getLogger();

        servlets.clear();

        logger.info("Initializing servlets...");
        servletClasses.values().forEach(this::initServlet);
        WebAPI.getExtensionService().loadPlugins("servlets", BaseServlet.class, this::initServlet);
    }
    private void initServlet(Class<? extends BaseServlet> servletClass) {
        Logger logger = WebAPI.getLogger();

        String basePath = servletClass.getAnnotation(Servlet.class).basePath();
        logger.info("  " + basePath + " -> " + servletClass.getName());

        if (basePath.contains("/")) {
            logger.error("  Base path is not allowed to have slashes");
            return;
        }

        // Create a new instance
        try {
            BaseServlet serv = servletClass.newInstance();
            servlets.put(basePath, serv);

            // Get all methods that are correctly annotated
            List<Tuple<Endpoint[], Method>> methods = Arrays.stream(servletClass.getMethods())
                    .filter(m -> m.isAnnotationPresent(Endpoint.class) ||
                            m.isAnnotationPresent(Endpoints.class))
                    .map(m -> new Tuple<>(m.getAnnotationsByType(Endpoint.class), m))
                    .collect(Collectors.toList());
            List<Tuple<Endpoint, Method>> newMethods = new ArrayList<>();

            for (Tuple<Endpoint[], Method> tuple : methods) {
                Endpoint[] routes = tuple.getFirst();
                Method method = tuple.getSecond();

                if (method.getParameterTypes()[0] != IServletData.class &&
                        method.getParameterTypes()[0] != ServletData.class) {
                    logger.error("    Method " + method.getName() + " first parameter is not of type IServletData");
                    continue;
                }

                method.setAccessible(true);
                for (Endpoint route : routes) {
                    logger.debug("    [" + route.method() + "] " + route.path() + " -> " + method.getName());
                    newMethods.add(new Tuple<>(route, method));
                }
            }

            servletMethods.put(serv, newMethods);
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("  -> Could not init servlet " + servletClass.getName() + ": " + e.getMessage());
            if (WebAPI.reportErrors()) WebAPI.sentryCapture(e);
        }
    }

    public Optional<MatchedRoute> getMethod(HttpMethod method, String path) {
        // First get the correct servlet
        List<String> pathParts = Util.getPathParts(path);
        if (pathParts.size() == 0) {
            return Optional.empty();
        }

        BaseServlet servlet = servlets.get(pathParts.get(0));
        if (servlet == null) {
            return Optional.empty();
        }

        // Then get the methods for that servlet
        List<Tuple<Endpoint, Method>> methods = servletMethods.get(servlet);

        // Find the most suitable method according to the path
        LinkedHashMap<String, String> bestMatches = null;
        Tuple<Endpoint, Method> bestTuple = null;

        pathParts = pathParts.subList(1, pathParts.size());
        for (Tuple<Endpoint, Method> tuple : methods) {
            Endpoint spec = tuple.getFirst();
            List<String> specPathParts = Util.getPathParts(spec.path());

            // Skip methods that don't match the verb or route
            if (spec.method() != method || specPathParts.size() != pathParts.size())
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

    public void registerServlet(Class<? extends BaseServlet> servlet) {
        Logger logger = WebAPI.getLogger();

        if (!servlet.isAnnotationPresent(Servlet.class)) {
            logger.error("Servlet " + servlet.getName() + " is missing @WebAPIServlet annotation");
            return;
        }

        Servlet info = servlet.getAnnotation(Servlet.class);
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

        try {
            Method m = servlet.getMethod("onRegister");
            m.invoke(null);
        } catch (NoSuchMethodException ignored) {
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            if (WebAPI.reportErrors()) WebAPI.sentryCapture(e);
        }

        servletClasses.put(info.basePath(), servlet);
    }

    @Override
    public Map<String, Class<? extends BaseServlet>> getLoadedServlets() {
        return servlets.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getClass()));
    }
}
