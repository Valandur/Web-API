package valandur.webapi.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import javafx.scene.web.WebEngine;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.text.Text;
import valandur.webapi.Permission;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.DataCache;
import valandur.webapi.misc.APICommandSource;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class CmdServlet extends APIServlet {
    @Override
    @Permission(perm = "cmd")
    protected Optional<CompletableFuture> handleGet(ServletData data) throws ServletException, IOException {
        data.setStatus(HttpServletResponse.SC_OK);

        APICommandSource cmdSource = new APICommandSource();

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

        return Optional.empty();
    }

    @Override
    @Permission(perm = "cmd")
    protected Optional<CompletableFuture> handlePost(ServletData data) throws ServletException, IOException {
        data.setStatus(HttpServletResponse.SC_OK);

        final JsonObject reqJson = (JsonObject) data.getAttribute("body");

        return Optional.of(CompletableFuture
                .supplyAsync(() -> reqJson.get("command").getAsString())
                .thenApplyAsync(WebAPI::executeCommand, DataCache.syncExecutor)
                .thenAcceptAsync((APICommandSource src) -> {
                    JsonArray arr = new JsonArray();
                    for (String line : src.getLines()) {
                        arr.add(new JsonPrimitive(line));
                    }
                    data.getJson().add("response", arr);
                }));
    }
}
