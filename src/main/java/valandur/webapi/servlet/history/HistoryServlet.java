package valandur.webapi.servlet.history;

import valandur.webapi.api.annotation.WebAPIRoute;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.servlet.IServlet;
import valandur.webapi.cache.DataCache;
import valandur.webapi.servlet.ServletData;

@WebAPIServlet(basePath = "history")
public class HistoryServlet implements IServlet {

    @WebAPIRoute(method = "GET", path = "/cmd", perm = "cmd")
    public void getCmds(ServletData data) {
        data.addJson("ok", true, false);
        data.addJson("calls", DataCache.getCommandCalls(), false);
    }

    @WebAPIRoute(method = "GET", path = "/chat", perm = "chat")
    public void getChat(ServletData data) {
        data.addJson("ok", true, false);
        data.addJson("messages", DataCache.getChatMessages(), false);
    }
}
