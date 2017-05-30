package valandur.webapi.handler;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import valandur.webapi.WebAPI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimitHandler extends AbstractHandler {

    private Map<String, Double> lastCall = new ConcurrentHashMap<>();

    public RateLimitHandler() {
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String key = request.getAttribute("key").toString();
        int limit = (int)request.getAttribute("rate");

        if (limit > 0) {
            double time = System.nanoTime() / 1000000000d;

            if (lastCall.containsKey(key) && time - lastCall.get(key) < 1d / limit) {
                WebAPI.getInstance().getLogger().warn(request.getRemoteAddr() + " has exceeded the rate limit when requesting " + request.getRequestURI());
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                baseRequest.setHandled(true);
                return;
            }

            lastCall.put(key, time);
        }
    }
}
