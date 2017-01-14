package valandur.webapi.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.text.Text;
import valandur.webapi.Permission;
import valandur.webapi.WebAPI;
import valandur.webapi.misc.WebAPICommandSource;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CmdServlet extends WebAPIServlet {
    @Override
    @Permission(perm = "cmd")
    protected void handleGet(ServletData data) {
        data.setStatus(HttpServletResponse.SC_OK);

        WebAPICommandSource cmdSource = new WebAPICommandSource();

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
        data.getJson().add("commands", arr);
    }

    @Override
    @Permission(perm = "cmd")
    protected void handlePost(ServletData data) {
        data.setStatus(HttpServletResponse.SC_OK);

        final JsonObject reqJson = (JsonObject) data.getAttribute("body");

        try {
            CompletableFuture
                .supplyAsync(() -> reqJson.get("command").getAsString())
                .thenApplyAsync(WebAPI::executeCommand, WebAPI.getInstance().syncExecutor)
                .thenAcceptAsync((WebAPICommandSource src) -> {
                    JsonArray arr = new JsonArray();
                    for (String line : src.getLines()) {
                        arr.add(new JsonPrimitive(line));
                    }
                    data.getJson().add("response", arr);
                }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
