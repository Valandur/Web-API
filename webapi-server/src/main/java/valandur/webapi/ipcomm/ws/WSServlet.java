package valandur.webapi.ipcomm.ws;


import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import valandur.webapi.ipcomm.IPLink;
import valandur.webapi.ipcomm.IPServlet;

public class WSServlet extends WebSocketServlet implements IPServlet<WSLink> {

    private WSLink link;


    @Override
    public void init(WSLink link) {
        this.link = link;
    }

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(10000);
        factory.setCreator(new WSCreator(link));
    }
}
