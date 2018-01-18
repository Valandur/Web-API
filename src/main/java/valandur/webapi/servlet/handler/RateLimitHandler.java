package valandur.webapi.servlet.handler;

import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import valandur.webapi.WebAPI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class RateLimitHandler extends AbstractHandler {

    private Map<String, Double> lastCall = new ConcurrentHashMap<>();
    private static long start = System.nanoTime();
    private static AtomicLong calls = new AtomicLong(0);


    public RateLimitHandler() {
        calls = new AtomicLong(0);
        start = System.nanoTime();
    }

    public static double getAverageCallsPerSecond() {
        double timeDiff = (System.nanoTime() - start) / 1000000000d;
        return calls.get() / timeDiff;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        // Don't count OPTIONS requests as actual requests
        if (request.getMethod().equalsIgnoreCase(HttpMethod.OPTIONS.asString()))
            return;

        calls.incrementAndGet();

        if (target.startsWith("/api/user")) {
            return;
        }

        String key = request.getAttribute("key").toString();
        int limit = (int)request.getAttribute("rate");

        if (limit > 0) {
            double time = System.nanoTime() / 1000000000d;

            if (lastCall.containsKey(key) && time - lastCall.get(key) < 1d / limit) {
                WebAPI.getLogger().warn(request.getRemoteAddr() + " has exceeded the rate limit when requesting " + request.getRequestURI());
                response.sendError(429, "Rate limit exceeded");
                baseRequest.setHandled(true);
                return;
            }

            lastCall.put(key, time);
        }
    }
}
