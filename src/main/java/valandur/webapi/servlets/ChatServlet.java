package valandur.webapi.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import valandur.webapi.Permission;
import valandur.webapi.cache.CachedChatMessage;
import valandur.webapi.cache.DataCache;
import valandur.webapi.misc.JsonConverter;

import javax.servlet.http.HttpServletResponse;

public class ChatServlet extends WebAPIServlet {
    @Override
    @Permission(perm = "chat")
    protected void handleGet(ServletData data) {
        data.setStatus(HttpServletResponse.SC_OK);

        JsonArray arr = new JsonArray();
        for (CachedChatMessage msg : DataCache.getChatMessages()) {
            arr.add(JsonConverter.cacheToJson(msg));
        }
        data.getJson().add("messages", arr);
    }
}
