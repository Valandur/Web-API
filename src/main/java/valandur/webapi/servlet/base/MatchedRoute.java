package valandur.webapi.servlet.base;

import valandur.webapi.WebAPI;
import valandur.webapi.api.servlet.Endpoint;
import valandur.webapi.api.servlet.Servlet;
import valandur.webapi.api.cache.entity.ICachedEntity;
import valandur.webapi.api.cache.player.ICachedPlayer;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.cache.entity.CachedEntity;
import valandur.webapi.cache.player.CachedPlayer;
import valandur.webapi.cache.world.CachedWorld;
import valandur.webapi.util.Util;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;

public class MatchedRoute {

    private Servlet servletSpec;
    public Servlet getServletSpec() {
        return servletSpec;
    }

    private BaseServlet servlet;
    public BaseServlet getServlet() {
        return servlet;
    }

    private Endpoint route;
    public Endpoint getRoute() {
        return route;
    }

    private Method method;
    public Method getMethod() {
        return method;
    }

    private Map<String, String> matchedParts;
    public Map<String, String> getMatchedParts() {
        return matchedParts;
    }

    private Integer argumentError;
    public Integer getArgumentError() {
        return argumentError;
    }

    private String argumentErrorMessage;
    public String getArgumentErrorMessage() {
        return argumentErrorMessage;
    }

    private List<Object> matchedParams;
    public List<Object> getMatchedParams() {
        return matchedParams;
    }


    public MatchedRoute(BaseServlet servlet, Endpoint route,
                        Method method, LinkedHashMap<String, String> matchedParts) {

        this.servletSpec = servlet.getClass().getAnnotation(Servlet.class);
        this.servlet = servlet;
        this.route = route;
        this.method = method;
        this.matchedParts = matchedParts;
        this.matchedParams = new ArrayList<>();
        this.argumentError = HttpServletResponse.SC_OK;

        int i = 0;
        Class[] types = Arrays.copyOfRange(method.getParameterTypes(), 1, method.getParameterTypes().length);
        for (Map.Entry<String, String> entry : matchedParts.entrySet()) {
            if (i >= types.length)
                break;

            Class type = types[i];
            if (type.equals(CachedPlayer.class) || type.equals(ICachedPlayer.class)) {
                String uuid = entry.getValue();
                if (!Util.isValidUUID(uuid)) {
                    argumentError = HttpServletResponse.SC_BAD_REQUEST;
                    argumentErrorMessage = "Invalid player UUID";
                    break;
                }

                Optional<ICachedPlayer> optPlayer = WebAPI.getCacheService().getPlayer(UUID.fromString(uuid));
                if (!optPlayer.isPresent()) {
                    argumentError = HttpServletResponse.SC_NOT_FOUND;
                    argumentErrorMessage = "Player with UUID '" + uuid + "' could not be found";
                    break;
                }

                matchedParams.add(optPlayer.get());
            } else if (type.equals(CachedWorld.class) || type.equals(ICachedWorld.class)) {
                String uuid = entry.getValue();
                if (!Util.isValidUUID(uuid)) {
                    argumentError = HttpServletResponse.SC_BAD_REQUEST;
                    argumentErrorMessage = "Invalid world UUID";
                    break;
                }

                Optional<ICachedWorld> optWorld = WebAPI.getCacheService().getWorld(UUID.fromString(uuid));
                if (!optWorld.isPresent()) {
                    argumentError = HttpServletResponse.SC_NOT_FOUND;
                    argumentErrorMessage = "World with UUID '" + uuid + "' could not be found";
                    break;
                }

                matchedParams.add(optWorld.get());
            } else if (type.equals(CachedEntity.class) || type.equals(ICachedEntity.class)) {
                String uuid = entry.getValue();
                if (!Util.isValidUUID(uuid)) {
                    argumentError = HttpServletResponse.SC_BAD_REQUEST;
                    argumentErrorMessage = "Invalid entity UUID";
                    break;
                }

                Optional<ICachedEntity> optEntity = WebAPI.getCacheService().getEntity(UUID.fromString(uuid));
                if (!optEntity.isPresent()) {
                    argumentError = HttpServletResponse.SC_NOT_FOUND;
                    argumentErrorMessage = "Entity with UUID '" + uuid + "' could not be found";
                    break;
                }

                matchedParams.add(optEntity.get());
            } else if (type.equals(UUID.class)) {
                String uuid = entry.getValue();
                if (!Util.isValidUUID(uuid)) {
                    argumentError = HttpServletResponse.SC_BAD_REQUEST;
                    argumentErrorMessage = "Invalid UUID";
                    break;
                }

                matchedParams.add(UUID.fromString(uuid));
            } else if (type.equals(Integer.class) || type.equals(int.class)) {
                matchedParams.add(Integer.parseInt(entry.getValue()));
            } else if (type.equals(Long.class) || type.equals(long.class)) {
                matchedParams.add(Long.parseLong(entry.getValue()));
            } else if (type.equals(Double.class) || type.equals(double.class)) {
                matchedParams.add(Double.parseDouble(entry.getValue()));
            } else if (type.equals(String.class)) {
                matchedParams.add(entry.getValue());
            } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
                matchedParams.add(Boolean.parseBoolean(entry.getValue()));
            } else {
                matchedParams.add(null);
            }

            i++;
        }
    }
}
