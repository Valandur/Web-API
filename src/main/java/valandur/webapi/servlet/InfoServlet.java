package valandur.webapi.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.jetty.http.HttpMethod;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import valandur.webapi.WebAPI;
import valandur.webapi.api.servlet.Endpoint;
import valandur.webapi.api.servlet.Servlet;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.server.ServerService;
import valandur.webapi.servlet.base.ServletData;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Servlet(basePath = "info")
public class InfoServlet extends BaseServlet {

    @Endpoint(method = HttpMethod.GET, path = "/", perm = "get")
    public void getInfo(ServletData data) {
        Optional<Boolean> optRes = WebAPI.runOnMain(() -> {
            Server server = Sponge.getServer();
            Platform platform = Sponge.getPlatform();

            data.addData("motd", server.getMotd().toPlain(), false);
            data.addData("players", server.getOnlinePlayers().size(), false);
            data.addData("maxPlayers", server.getMaxPlayers(), false);
            data.addData("address", server.getBoundAddress().map(Object::toString).orElse(null), false);
            data.addData("onlineMode", server.getOnlineMode(), false);
            data.addData("resourcePack", server.getDefaultResourcePack().orElse(null), false);
            data.addData("hasWhitelist", server.hasWhitelist(), false);

            data.addData("uptimeTicks", server.getRunningTimeTicks(), false);
            data.addData("tps", server.getTicksPerSecond(), false);
            data.addData("minecraftVersion", platform.getMinecraftVersion().getName(), false);

            data.addData("game", platform.getContainer(Platform.Component.GAME), true);
            data.addData("api", platform.getContainer(Platform.Component.API), true);
            data.addData("implementation", platform.getContainer(Platform.Component.IMPLEMENTATION), true);
            return true;
        });

        data.addData("ok", optRes.orElse(false), false);
    }

    @Endpoint(method = HttpMethod.GET, path = "/properties", perm = "properties.list")
    public void getProperties(ServletData data) {
        ServerService srv = WebAPI.getServerService();

        data.addData("ok", true, false);
        data.addData("properties", srv.getProperties(), true);
    }

    @Endpoint(method = HttpMethod.POST, path = "/properties", perm = "properties.set")
    public void setProperties(ServletData data) {
        ServerService srv = WebAPI.getServerService();

        JsonNode body = data.getRequestBody();
        JsonNode props = body.get("properties");

        if (props == null) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid new properties");
            return;
        }

        for (Iterator<String> it = props.fieldNames(); it.hasNext(); ) {
            String key = it.next();
            srv.setProperty(key, props.get(key).asText());
        }

        data.addData("ok", true, false);
        data.addData("properties", srv.getProperties(), true);
    }

    @Endpoint(method = HttpMethod.GET, path = "/stats", perm = "stats")
    public void getStats(ServletData data) {
        data.addData("ok", true, true);
        data.addData("tps", serverService.getAverageTps(), false);
        data.addData("players", serverService.getOnlinePlayers(), false);
        data.addData("cpu", serverService.getCpuLoad(), false);
        data.addData("memory", serverService.getMemoryLoad(), false);
        data.addData("disk", serverService.getDiskUsage(), false);
    }
}
