package valandur.webapi.servlet;

import valandur.webapi.annotation.WebAPISpec;
import valandur.webapi.cache.DataCache;

public class HistoryServlet extends WebAPIServlet {

    @WebAPISpec(method = "GET", path = "/cmd", perm = "history.get")
    public void getCmds(ServletData data) {
        data.addJson("ok", true, false);
        data.addJson("calls", DataCache.getCommandCalls(), false);
    }

    @WebAPISpec(method = "GET", path = "/chat", perm = "history.get")
    public void getChat(ServletData data) {
        data.addJson("ok", true, false);
        data.addJson("messages", DataCache.getChatMessages(), false);
    }
}
