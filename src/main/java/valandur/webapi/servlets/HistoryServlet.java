package valandur.webapi.servlets;

import valandur.webapi.misc.Permission;
import valandur.webapi.cache.DataCache;
import valandur.webapi.json.JsonConverter;

import javax.servlet.http.HttpServletResponse;

public class HistoryServlet extends WebAPIServlet {
    @Override
    @Permission(perm = "history")
    protected void handleGet(ServletData data) {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        switch (paths[0].toLowerCase()) {
            case "cmd":
                data.setStatus(HttpServletResponse.SC_OK);
                data.addJson("calls", JsonConverter.toJson(DataCache.getCommandCalls()));
                break;

            case "chat":
                data.setStatus(HttpServletResponse.SC_OK);
                data.addJson("messages", JsonConverter.toJson(DataCache.getChatMessages()));
                break;

            default:
                data.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }
}
