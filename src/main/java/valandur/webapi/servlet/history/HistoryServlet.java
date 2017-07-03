package valandur.webapi.servlet.history;

import valandur.webapi.api.annotation.WebAPIEndpoint;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.servlet.WebAPIBaseServlet;
import valandur.webapi.servlet.ServletData;

@WebAPIServlet(basePath = "history")
public class HistoryServlet extends WebAPIBaseServlet {

    @WebAPIEndpoint(method = "GET", path = "/cmd", perm = "cmd")
    public void getCmds(ServletData data) {
        data.addJson("ok", true, false);
        data.addJson("calls", cacheService.getCommandCalls(), false);
    }

    @WebAPIEndpoint(method = "GET", path = "/chat", perm = "chat")
    public void getChat(ServletData data) {
        data.addJson("ok", true, false);
        data.addJson("messages", cacheService.getChatMessages(), false);
    }
}
