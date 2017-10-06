package valandur.webapi.servlet;

import org.eclipse.jetty.http.HttpMethod;
import valandur.webapi.api.servlet.Endpoint;
import valandur.webapi.api.servlet.Servlet;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.servlet.base.ServletData;

@Servlet(basePath = "history")
public class HistoryServlet extends BaseServlet {

    @Endpoint(method = HttpMethod.GET, path = "/cmd", perm = "cmd")
    public void getCmds(ServletData data) {
        data.addJson("ok", true, false);
        data.addJson("calls", cacheService.getCommandCalls(), false);
    }

    @Endpoint(method = HttpMethod.GET, path = "/chat", perm = "chat")
    public void getChat(ServletData data) {
        data.addJson("ok", true, false);
        data.addJson("messages", cacheService.getChatMessages(), false);
    }
}
