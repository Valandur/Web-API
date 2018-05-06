package valandur.webapi.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import valandur.webapi.api.cache.plugin.ICachedPluginContainer;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.ExplicitDetails;
import valandur.webapi.api.servlet.Permission;
import valandur.webapi.security.SecurityContext;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Path("plugin")
@Api(tags = { "Plugin" }, value = "List all plugins and get detailed information about them.")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class PluginServlet extends BaseServlet {

    @GET
    @ExplicitDetails
    @Permission("list")
    @ApiOperation(value = "List plugins", notes = "Get a list of all the plugins running on the server.")
    public Collection<ICachedPluginContainer> listPlugins() {
        return cacheService.getPlugins();
    }

    @GET
    @Path("/{plugin}")
    @Permission("one")
    @ApiOperation(value = "Get a plugin", notes = "Gets detailed information about a plugin.")
    public ICachedPluginContainer getPlugin(
            @PathParam("plugin") @ApiParam("The id of the plugin") String pluginName)
            throws NotFoundException {
        Optional<ICachedPluginContainer> optPlugin = cacheService.getPlugin(pluginName);
        if (!optPlugin.isPresent()) {
            throw new NotFoundException("Plugin with id '" + pluginName + "' could not be found");
        }

        return optPlugin.get();
    }

    @PUT
    @Path("/{plugin}")
    @Permission("toggle")
    @ApiOperation(value = "Toggle a plugin", notes = "Allows enabling/disabling a plugin/mod. Requires a server restart.")
    public ICachedPluginContainer togglePlugin(
            @PathParam("plugin") @ApiParam("The id of the plugin") String pluginName)
            throws NotFoundException {

        Optional<ICachedPluginContainer> optPlugin = cacheService.getPlugin(pluginName);
        if (!optPlugin.isPresent()) {
            throw new NotFoundException("Plugin with id '" + pluginName + "' could not be found");
        }

        ICachedPluginContainer plugin = optPlugin.get();

        if (!plugin.toggle()) {
            throw new InternalServerErrorException("Could not toggle plugin");
        }

        return plugin;
    }

    @GET
    @Path("/{plugin}/config")
    @Permission({ "config", "get" })
    @ApiOperation(value = "Get plugin configs", notes = "Gets a map containing the plugin config file names as keys, " +
            "and their config file contents as their values.")
    public Map<String, Object> getPluginConfig(
            @PathParam("plugin") @ApiParam("The id of the plugin") String pluginName)
            throws NotFoundException {
        Optional<ICachedPluginContainer> optPlugin = cacheService.getPlugin(pluginName);
        if (!optPlugin.isPresent()) {
            throw new NotFoundException("Plugin with id '" + pluginName + "' could not be found");
        }

        List<java.nio.file.Path> paths = getConfigFiles(optPlugin.get());

        Map<String, Object> configs = new HashMap<>();
        for (java.nio.file.Path path : paths) {
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

        return configs;
    }

    @POST
    @Path("/{plugin}/config")
    @Permission({ "config", "modify" })
    @Permission(value = { "config", "modify", "[plugin]" }, autoCheck = false)
    @ApiOperation(value = "Change plugin configs", notes = "Allows changing the config files of plugin. Send a map " +
            "from config filename to file contents. **This does not reload the plugin**, you can do that with " +
            "`sponge plugins reload`, but not all plugins implement the reload event.")
    public Map<String, Object> changePluginConfig(
            @PathParam("plugin") @ApiParam("The id of the plugin") String pluginName,
            Map<String, Object> configs,
            @Context HttpServletRequest request)
            throws NotFoundException {

        if (configs == null) {
            throw new BadRequestException("Request body is required");
        }

        Optional<ICachedPluginContainer> optPlugin = cacheService.getPlugin(pluginName);
        if (!optPlugin.isPresent()) {
            throw new NotFoundException("Plugin with id '" + pluginName + "' could not be found");
        }

        SecurityContext context = (SecurityContext)request.getAttribute("security");
        if (!context.hasPerms(pluginName)) {
            throw new ForbiddenException("You do not have permission edit " + pluginName + " configs");
        }

        List<java.nio.file.Path> paths = getConfigFiles(optPlugin.get());
        for (java.nio.file.Path path : paths) {
            Object node = configs.get(path.getFileName().toString());
            if (node == null) continue;

            try {
                java.nio.file.Path newPath = path.getParent().resolve(path.getFileName().toString() + ".bck");
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

        return configs;
    }

    private List<java.nio.file.Path> getConfigFiles(ICachedPluginContainer plugin) {
        List<java.nio.file.Path> paths = new ArrayList<>();
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
