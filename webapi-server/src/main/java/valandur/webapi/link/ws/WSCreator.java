package valandur.webapi.link.ws;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

public class WSCreator implements WebSocketCreator {

    @Override
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
        for (String subprotocol : req.getSubProtocols())
        {
            if ("text".equals(subprotocol) || "binary".equals(subprotocol)) {
                resp.setAcceptedSubProtocol(subprotocol);
                return new WSMainSocket(WSLinkServer.instance);
            }
        }

        // No valid subprotocol in request, ignore the request
        return null;
    }
}
