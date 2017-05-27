package valandur.webapi.servlet;

import valandur.webapi.permission.Permission;
import valandur.webapi.cache.DataCache;

import javax.servlet.http.HttpServletResponse;

public class HistoryServlet extends WebAPIServlet {
    @Override
    @Permission(perm = "history.get")
    protected void handleGet(ServletData data) {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "History endpoint type is missing");
            return;
        }

        String endpoint = paths[0].toLowerCase();
        switch (endpoint) {
            case "cmd":
                data.addJson("calls", DataCache.getCommandCalls(), false);
                break;

            case "chat":
                data.addJson("messages", DataCache.getChatMessages(), false);
                break;

            default:
                data.sendError(HttpServletResponse.SC_NOT_FOUND, "Unkown history endpoint '" + endpoint + "'");
                break;
        }
    }
}
