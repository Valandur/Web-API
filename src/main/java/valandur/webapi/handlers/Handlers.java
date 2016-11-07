package valandur.webapi.handlers;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

import java.util.ArrayList;
import java.util.List;

public class Handlers {

    public static Handler get() {
        List<ContextHandler> handlers = new ArrayList<ContextHandler>();

        handlers.add( newContext("/", new IndexHandler(handlers)) );
        handlers.add( newContext("/info", new InfoHandler()) );
        handlers.add( newContext("/cmd", new CmdHandler()) );
        handlers.add( newContext("/players", new PlayerHandler()) );
        handlers.add( newContext("/worlds", new WorldHandler()) );
        handlers.add( newContext("/chat", new ChatHandler()) );
        handlers.add( newContext("/plugins", new PluginHandler()) );

        ContextHandlerCollection coll = new ContextHandlerCollection();
        coll.setHandlers(handlers.toArray(new Handler[handlers.size()]));
        return coll;
    }

    private static ContextHandler newContext(String path, Handler handler) {
        ContextHandler context = new ContextHandler();
        context.setContextPath(path);
        context.setHandler(handler);
        return context;
    }
}
