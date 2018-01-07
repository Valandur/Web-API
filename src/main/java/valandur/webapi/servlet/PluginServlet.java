package valandur.webapi.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.eclipse.jetty.http.HttpMethod;
import valandur.webapi.WebAPI;
import valandur.webapi.api.servlet.Endpoint;
import valandur.webapi.api.servlet.Servlet;
import valandur.webapi.api.cache.plugin.ICachedPluginContainer;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.servlet.base.ServletData;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Servlet(basePath = "plugin")
public class PluginServlet extends BaseServlet {

    @Endpoint(method = HttpMethod.GET, path = "/", perm = "list")
    public void getPlugins(ServletData data) {
        data.addData("ok", true, false);
        data.addData("plugins", cacheService.getPlugins(), data.getQueryParam("details").isPresent());
    }

    @Endpoint(method = HttpMethod.GET, path = "/:plugin", perm = "one")
    public void getPlugin(ServletData data, String pluginName) {
        Optional<ICachedPluginContainer> optPlugin = cacheService.getPlugin(pluginName);
        if (!optPlugin.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Plugin with id '" + pluginName + "' could not be found");
            return;
        }

        data.addData("ok", true, false);
        data.addData("plugin", optPlugin.get(), true);
    }

    @Endpoint(method = HttpMethod.GET, path = "/:plugin/config", perm = "config.one")
    public void getPluginConfig(ServletData data, String pluginName) {
        Optional<ICachedPluginContainer> optPlugin = cacheService.getPlugin(pluginName);
        if (!optPlugin.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Plugin with id '" + pluginName + "' could not be found");
            return;
        }

        List<Path> paths = getConfigFiles(optPlugin.get());

        Map<String, Object> configs = new HashMap<>();
        for (Path path : paths) {
            String key = path.getFileName().toString();

            try {
                ConfigurationLoader<CommentedConfigurationNode> loader =
                        HoconConfigurationLoader.builder().setPath(path).build();
                CommentedConfigurationNode config = loader.load();
                configs.put(key, parseConfiguration(config));
            } catch (IOException e) {
                configs.put(key, e);
            }
        }

        data.addData("ok", true, false);
        data.addData("configs", configs, true);
    }

    @Endpoint(method = HttpMethod.POST, path = ":plugin/config", perm = "config.change")
    public void changePluginConfig(ServletData data, String pluginName) {
        Optional<ICachedPluginContainer> optPlugin = cacheService.getPlugin(pluginName);
        if (!optPlugin.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Plugin with id '" + pluginName + "' could not be found");
            return;
        }

        JsonNode configs = data.getRequestBody();

        List<Path> paths = getConfigFiles(optPlugin.get());
        for (Path path : paths) {
            JsonNode node = configs.get(path.getFileName().toString());
            if (node == null) continue;

            try {
                Path newPath = path.getParent().resolve(path.getFileName().toString() + ".bck");
                WebAPI.getLogger().info(newPath.toString());
                if (!Files.exists(newPath)) {
                    Files.copy(path, newPath);
                }

                ObjectMapper om = new ObjectMapper();
                om.enable(SerializationFeature.INDENT_OUTPUT);
                om.writeValue(path.toFile(), node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        data.addData("ok", true, false);
        data.addData("configs", configs, true);
    }

    private List<Path> getConfigFiles(ICachedPluginContainer plugin) {
        List<Path> paths = new ArrayList<>();
        paths.add(Paths.get("config/" + plugin.getId() + ".conf"));
        try {
            Files.walk(Paths.get("config/" + plugin.getId() + "/")).filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".conf")).forEach(paths::add);
        } catch (IOException ignored) {}
        return paths;
    }

    private Object parseConfiguration(CommentedConfigurationNode config) {
        if (config.hasListChildren()) {
            List<Object> cfg = new ArrayList<>();
            for (CommentedConfigurationNode node : config.getChildrenList()) {
                cfg.add(parseConfiguration(node));
            }
            return cfg;
        } else if (config.hasMapChildren()) {
            Map<String, Object> cfg = new HashMap<>();
            for (CommentedConfigurationNode node : config.getChildrenMap().values()) {
                cfg.put(node.getKey().toString(), parseConfiguration(node));
            }
            return cfg;
        }

        return config.getValue();
    }
}
