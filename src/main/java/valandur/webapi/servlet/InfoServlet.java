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
import valandur.webapi.api.cache.plugin.ICachedPluginContainer;
import valandur.webapi.api.server.IServerStat;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Permission;
import valandur.webapi.cache.plugin.CachedPluginContainer;
import valandur.webapi.server.ServerService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Path("/stats")
    @Permission("stats")
    @ApiOperation(
            value = "Server stats",
            notes = "Get statistical information about the server, such as player count, " +
                    "cpu and memory usage over time.")
    public ServerStats getStats(@QueryParam("limit") Integer limit) {
        if (limit != null) {
            return new ServerStats(limit);
        }
        return new ServerStats();
    }

    @GET
    @Path("/servlets")
    @Permission("servlets")
    @ApiOperation(
            value = "List servlets",
            notes = "Lists all the active servlets running in the Web-API")
    public Map<String, String> listServlets() {
        return servletService.getRegisteredServlets().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getName()));
    }


    @ApiModel("ServerInfo")
        public static class ServerInfo {

        private Text motd;
        @ApiModelProperty(value = "The message of the day set on the server", required = true)
        public Text getMotd() {
            return motd;
        }

        private int players;
        @ApiModelProperty(value = "The amount of players currently playing on the server", required = true)
        public int getPlayers() {
            return players;
        }

        private int maxPlayers;
        @ApiModelProperty(value = "The maximum amount of players allowed on the server", required = true)
        public int getMaxPlayers() {
            return maxPlayers;
        }

        private String address;
        @ApiModelProperty("The address that the server is bound to")
        public String getAddress() {
            return address;
        }

        private boolean onlineMode;
        @ApiModelProperty(value = "True if the server is in online mode and verifies connections, false otherwise", required = true)
        public boolean isOnlineMode() {
            return onlineMode;
        }

        private String resourcePack;
        @ApiModelProperty("The name of the resource pack this is used on the server")
        public String getResourcePack() {
            return resourcePack;
        }

        private boolean hasWhitelist;
        @ApiModelProperty(value = "True if the server has activated the whitelist, false otherwise", required = true)
        public boolean isHasWhitelist() {
            return hasWhitelist;
        }

        private int uptimeTicks;
        @ApiModelProperty(value = "The number of ticks the server has been running", required = true)
        public int getUptimeTicks() {
            return uptimeTicks;
        }

        private double tps;
        @ApiModelProperty(value = "The average ticks per second the server is running with", required = true)
        public double getTps() {
            return tps;
        }

        private String minecraftVersion;
        @ApiModelProperty(value = "The Minecraft version running on the server", required = true)
        public String getMinecraftVersion() {
            return minecraftVersion;
        }

        private ICachedPluginContainer game;
        @ApiModelProperty(required = true)
        public ICachedPluginContainer getGame() {
            return game;
        }

        private ICachedPluginContainer api;
        @ApiModelProperty(required = true)
        public ICachedPluginContainer getApi() {
            return api;
        }

        private ICachedPluginContainer implementation;
        @ApiModelProperty(required = true)
        public ICachedPluginContainer getImplementation() {
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

    @ApiModel("ServerStats")
        public static class ServerStats {

        private List<IServerStat<Double>> tps;
        @ApiModelProperty(value = "Historic values for the average ticks per second", required = true)
        public List<IServerStat<Double>> getTps() {
            return tps;
        }

        private List<IServerStat<Integer>> players;
        @ApiModelProperty(value = "Historic values for the number of online players", required = true)
        public List<IServerStat<Integer>> getPlayers() {
            return players;
        }

        private List<IServerStat<Double>> cpu;
        @ApiModelProperty(value = "Historic values for the cpu load", required = true)
        public List<IServerStat<Double>> getCpu() {
            return cpu;
        }

        private List<IServerStat<Double>> memory;
        @ApiModelProperty(value = "Historic values for the memory load", required = true)
        public List<IServerStat<Double>> getMemory() {
            return memory;
        }

        private List<IServerStat<Double>> disk;
        @ApiModelProperty(value = "Historic values for the disk usage", required = true)
        public List<IServerStat<Double>> getDisk() {
            return disk;
        }


        public ServerStats() {
            this(Integer.MAX_VALUE);
        }
        public ServerStats(int limit) {
            ServerService srv = WebAPI.getServerService();
            int size = srv.getNumEntries();
            int l = Math.min(limit, size);

            this.tps = srv.getAverageTps().subList(size - l, size);
            this.players = srv.getOnlinePlayers().subList(size - l, size);
            this.cpu = srv.getCpuLoad().subList(size - l, size);
            this.memory = srv.getMemoryLoad().subList(size - l, size);
            this.disk = srv.getDiskUsage().subList(size - l, size);
        }
    }
}
