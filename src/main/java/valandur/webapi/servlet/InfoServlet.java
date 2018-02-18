package valandur.webapi.servlet;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.resourcepack.ResourcePack;
import org.spongepowered.api.text.Text;
import valandur.webapi.WebAPI;
import valandur.webapi.api.server.IServerStat;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Permission;
import valandur.webapi.cache.plugin.CachedPluginContainer;
import valandur.webapi.security.SecurityContext;
import valandur.webapi.server.ServerProperty;
import valandur.webapi.server.ServerService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Path("info")
@Api(tags = { "Info" }, value = "Get information and stats about the Minecraft server")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class InfoServlet extends BaseServlet {

    @GET
    @Permission("info")
    @ApiOperation(
            value = "Server info",
            notes = "Get general information about the Minecraft server.")
    public ServerInfo getInfo() {
        return WebAPI.runOnMain(ServerInfo::new);
    }

    @GET
    @Path("/properties")
    @Permission({ "properties", "list" })
    @ApiOperation(
            value = "List server properties",
            notes = "Get the main server properties (server.properties file).")
    public Collection<ServerProperty> listProperties() {
        ServerService srv = WebAPI.getServerService();
        return srv.getProperties();
    }

    @PUT
    @Path("/properties")
    @Permission({ "properties", "modify" })
    @Permission(value = { "properties", "modify", "[property]" }, autoCheck = false)
    @ApiOperation(
            value = "Modify server properties",
            notes = "Modify settings in the server.properties file. **Note that these settings don't take effect " +
                    "until you restart the server.")
    public Collection<ServerProperty> modifyProperties(
            final Map<String, String> properties,
            @Context HttpServletRequest request)
            throws BadRequestException {

        if (properties == null) {
            throw new BadRequestException("Request body is required");
        }

        SecurityContext context = (SecurityContext)request.getAttribute("security");

        ServerService srv = WebAPI.getServerService();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (!context.hasPerms(entry.getKey())) {
                throw new ForbiddenException("You do not have permission to change the " + entry.getKey() + " setting");
            }
            srv.setProperty(entry.getKey(), entry.getValue());
        }

        return srv.getProperties();
    }

    @GET
    @Path("/stats")
    @Permission("stats")
    @ApiOperation(
            value = "Server stats",
            notes = "Get statistical information about the server, such as player count, " +
                    "cpu and memory usage over time.")
    public ServerStats getStats() {
        return new ServerStats();
    }


    @ApiModel(value = "ServerInfo")
    public static class ServerInfo {

        private Text motd;
        @ApiModelProperty(value = "The message of the day set on the server")
        public Text getMotd() {
            return motd;
        }

        private int players;
        @ApiModelProperty(value = "The amount of players currently playing on the server")
        public int getPlayers() {
            return players;
        }

        private int maxPlayers;
        @ApiModelProperty(value = "The maximum amount of players allowed on the server")
        public int getMaxPlayers() {
            return maxPlayers;
        }

        private String address;
        @ApiModelProperty(value = "The address that the server is bound to")
        public String getAddress() {
            return address;
        }

        private boolean onlineMode;
        @ApiModelProperty(value = "True if the server is in online mode and verifies connections, false otherwise")
        public boolean isOnlineMode() {
            return onlineMode;
        }

        private String resourcePack;
        @ApiModelProperty(value = "The name of the resource pack this is used on the server")
        public String getResourcePack() {
            return resourcePack;
        }

        private boolean hasWhitelist;
        @ApiModelProperty(value = "True if the server has activated the whitelist, false otherwise")
        public boolean isHasWhitelist() {
            return hasWhitelist;
        }

        private int uptimeTicks;
        @ApiModelProperty(value = "The number of ticks the server has been running")
        public int getUptimeTicks() {
            return uptimeTicks;
        }

        private double tps;
        @ApiModelProperty(value = "The average ticks per second the server is running with")
        public double getTps() {
            return tps;
        }

        private String minecraftVersion;
        @ApiModelProperty(value = "The Minecraft version running on the server")
        public String getMinecraftVersion() {
            return minecraftVersion;
        }

        private CachedPluginContainer game;
        public CachedPluginContainer getGame() {
            return game;
        }

        private CachedPluginContainer api;
        public CachedPluginContainer getApi() {
            return api;
        }

        private CachedPluginContainer implementation;
        public CachedPluginContainer getImplementation() {
            return implementation;
        }

        public ServerInfo() {
            Server server = Sponge.getServer();
            Platform platform = Sponge.getPlatform();

            this.motd = server.getMotd().toBuilder().build();
            this.players = server.getOnlinePlayers().size();
            this.maxPlayers = server.getMaxPlayers();
            this.address = server.getBoundAddress().map(Object::toString).orElse(null);
            this.onlineMode = server.getOnlineMode();
            this.resourcePack = server.getDefaultResourcePack().map(ResourcePack::getName).orElse(null);
            this.hasWhitelist = server.hasWhitelist();

            this.uptimeTicks = server.getRunningTimeTicks();
            this.tps = server.getTicksPerSecond();
            this.minecraftVersion = platform.getMinecraftVersion().getName();

            this.game = new CachedPluginContainer(platform.getContainer(Platform.Component.GAME));
            this.api = new CachedPluginContainer(platform.getContainer(Platform.Component.API));
            this.implementation = new CachedPluginContainer(platform.getContainer(Platform.Component.IMPLEMENTATION));
        }
    }

    @ApiModel(value = "ServerStats")
    public static class ServerStats {

        private List<IServerStat<Double>> tps;
        @ApiModelProperty(value = "Historic values for the average ticks per second")
        public List<IServerStat<Double>> getTps() {
            return tps;
        }

        private List<IServerStat<Integer>> players;
        @ApiModelProperty(value = "Historic values for the number of online players")
        public List<IServerStat<Integer>> getPlayers() {
            return players;
        }

        private List<IServerStat<Double>> cpu;
        @ApiModelProperty(value = "Historic values for the cpu load")
        public List<IServerStat<Double>> getCpu() {
            return cpu;
        }

        private List<IServerStat<Double>> memory;
        @ApiModelProperty(value = "Historic values for the memory load")
        public List<IServerStat<Double>> getMemory() {
            return memory;
        }

        private List<IServerStat<Double>> disk;
        @ApiModelProperty(value = "Historic values for the disk usage")
        public List<IServerStat<Double>> getDisk() {
            return disk;
        }

        public ServerStats() {
            ServerService srv = WebAPI.getServerService();
            this.tps = srv.getAverageTps();
            this.players = srv.getOnlinePlayers();
            this.cpu = srv.getCpuLoad();
            this.memory = srv.getMemoryLoad();
            this.disk = srv.getDiskUsage();
        }
    }
}
