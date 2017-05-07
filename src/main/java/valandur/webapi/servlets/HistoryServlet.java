package valandur.webapi.servlets;

import valandur.webapi.misc.Permission;
import valandur.webapi.cache.DataCache;
import valandur.webapi.json.JsonConverter;

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
                data.addJson("calls", JsonConverter.toJson(DataCache.getCommandCalls()));
                break;

            case "chat":
                data.addJson("messages", JsonConverter.toJson(DataCache.getChatMessages()));
                break;

            default:
                data.sendError(HttpServletResponse.SC_NOT_FOUND, "Unkown history endpoint '" + endpoint + "'");
                break;
        }
    }
}
