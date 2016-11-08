package valandur.webapi.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.text.Text;
import valandur.webapi.misc.CustomCommandSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class CmdServlet extends APIServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject json = new JsonObject();
        resp.setContentType("application/json; charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        CustomCommandSource cmdSource = new CustomCommandSource();
        String[] paths = this.getPathParts(req);

        if (paths.length == 0) {
            JsonArray arr = new JsonArray();
            Collection<CommandMapping> cmds = Sponge.getCommandManager().getAll().values();
            for (CommandMapping cmd : cmds) {
                JsonObject obj = new JsonObject();
                obj.addProperty("name", cmd.getPrimaryAlias());
                Set<String> aliases = cmd.getAllAliases();
                JsonArray jsonAliases = new JsonArray();
                for (String alias : aliases) {
                    jsonAliases.add(new JsonPrimitive(alias));
                }
                obj.add("aliases", jsonAliases);
                obj.addProperty("usage", cmd.getCallable().getUsage(cmdSource).toPlain());
                Optional<Text> descr = cmd.getCallable().getShortDescription(cmdSource);
                obj.addProperty("description", descr.isPresent() ? descr.get().toPlain() : null);

                arr.add(obj);
            }
            json.add("commands", arr);
        } else {
            CommandManager cmdManager = Sponge.getGame().getCommandManager();
            cmdManager.process(cmdSource, String.join(" ", paths));

            JsonArray arr = new JsonArray();
            for (String line : cmdSource.getLines()) {
                arr.add(new JsonPrimitive(line));
            }
            json.add("response", arr);
        }

        PrintWriter out = resp.getWriter();
        out.print(json);
    }
}
