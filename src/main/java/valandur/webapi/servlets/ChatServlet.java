package valandur.webapi.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.tuple.Triple;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import valandur.webapi.WebAPI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class ChatServlet extends APIServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject json = new JsonObject();
        resp.setContentType("application/json; charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        JsonArray arr = new JsonArray();
        for (Triple<Date, Player, Text> msg : WebAPI.getChatMessages()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("timestamp", msg.getLeft().toString());
            obj.addProperty("sender", msg.getMiddle().getName());
            obj.addProperty("message", msg.getRight().toPlain());
            arr.add(obj);
        }
        json.add("messages", arr);

        PrintWriter out = resp.getWriter();
        out.print(json);
    }
}
