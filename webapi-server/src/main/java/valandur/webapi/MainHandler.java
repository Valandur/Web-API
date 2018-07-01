package valandur.webapi;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import valandur.webapi.link.LinkServer;
import valandur.webapi.link.message.RequestMessage;
import valandur.webapi.link.message.ResponseMessage;
import valandur.webapi.util.Util;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MainHandler extends AbstractHandler {

    private static final String API_KEY_HEADER = "X-WEBAPI-KEY";
    private static final String SERVER_HEADER = "X-WEBAPI-SERVER";

    private LinkServer link;
    private static Map<String, AsyncContext> contexts = new ConcurrentHashMap<>();


    public MainHandler(LinkServer link) {
        this.link = link;
        link.onResponse((ResponseMessage res) -> {
            AsyncContext ctx = contexts.get(res.getId());
            if (ctx == null) {
                System.out.println("ERROR: Could not find context for " + res.getId());
                return;
            }

            HttpServletResponse resp = (HttpServletResponse)ctx.getResponse();
            if (resp == null) {
                System.out.println("ERROR: Could not find response object for " + res.getId());
                return;
            }

            try {
                resp.setStatus(res.getStatus() != 0 ? res.getStatus() : HttpServletResponse.SC_OK);
                res.getHeaders().forEach(resp::setHeader);
                resp.getWriter().write(res.getMessage());
                ctx.complete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        System.out.println(target);
        baseRequest.setHandled(true);

        Map<String, String> params = Util.getQueryParams(req);

        String key = req.getHeader(API_KEY_HEADER);
        String server = req.getHeader(SERVER_HEADER);

        if (server == null || server.isEmpty()) {
            server = params.get("server");
            if (server == null || server.isEmpty()) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "No server specified in 'X-WEBAPI-SERVER' header or 'server' query parameter.");
                return;
            }
        }

        // Remove our query param in case it is set
        params.remove("server");

        if (!link.hasServer(server)) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "Could not find server '" + server + "'");
            return;
        }

        if (!link.isConnected(server)) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "Server '" + server + "' is currently not connected");
            return;
        }

        Map<String, String> headers = new HashMap<>();
        Enumeration<String> hs = req.getHeaderNames();
        while (hs.hasMoreElements()) {
            String headerName = hs.nextElement();
            headers.put(headerName, req.getHeader(headerName));
        }

        // Remove our header if it's set
        headers.remove(SERVER_HEADER);

        String id = Util.generateUniqueId();
        AsyncContext ctx = baseRequest.startAsync();
        contexts.put(id, ctx);

        String body = req.getReader().lines().collect(Collectors.joining());
        RequestMessage msg = new RequestMessage(id, req.getRemoteAddr(), req.getMethod(),
                "/api" + target, headers, params, body);

        link.sendRequest(server, msg);
    }
}
