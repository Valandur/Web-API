package valandur.webapi.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.tuple.Triple;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import valandur.webapi.Permission;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.CachedChatMessage;
import valandur.webapi.cache.DataCache;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ChatServlet extends APIServlet {
    @Override
    @Permission(perm = "chat")
    protected Optional<CompletableFuture> handleGet(ServletData data) throws ServletException, IOException {
        data.setStatus(HttpServletResponse.SC_OK);

        JsonArray arr = new JsonArray();
        for (CachedChatMessage msg : DataCache.chatMessages) {
            JsonObject obj = new JsonObject();
            obj.addProperty("timestamp", msg.getDate().toString());
            obj.addProperty("sender", msg.getSender().getName());
            obj.addProperty("message", msg.getMessage().toPlain());
            arr.add(obj);
        }
        data.getJson().add("messages", arr);

        return Optional.empty();
    }
}
