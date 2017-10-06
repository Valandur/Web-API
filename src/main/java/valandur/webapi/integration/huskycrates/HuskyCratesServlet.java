package valandur.webapi.integration.huskycrates;

import com.codehusky.huskycrates.HuskyCrates;
import com.codehusky.huskycrates.crate.VirtualCrate;
import com.codehusky.huskycrates.crate.config.CrateReward;
import org.eclipse.jetty.http.HttpMethod;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import valandur.webapi.api.WebAPIAPI;
import valandur.webapi.api.json.IJsonService;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Endpoint;
import valandur.webapi.api.servlet.IServletData;
import valandur.webapi.api.servlet.Servlet;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Servlet(basePath = "husky")
public class HuskyCratesServlet extends BaseServlet {

    public static void onRegister() {
        IJsonService json = WebAPIAPI.getJsonService().get();
        json.registerCache(VirtualCrate.class, CachedVirtualCrate.class);
        json.registerCache(CrateReward.class, CachedCrateReward.class);
    }


    private HuskyCrates getHuskyPlugin(IServletData data) {
        Optional<PluginContainer> optContainer = Sponge.getPluginManager().getPlugin("huskycrates");
        if (!optContainer.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "HuskyCrates plugin not found");
            return null;
        }

        Optional<?> optPlugin = optContainer.get().getInstance();
        if (!optPlugin.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "HuskyCrates plugin instance not found");
            return null;
        }

        return (HuskyCrates)optPlugin.get();
    }

    @Endpoint(method = HttpMethod.GET, path = "crate", perm = "list")
    public void getCrates(IServletData data) {
        HuskyCrates plugin = getHuskyPlugin(data);
        if (plugin == null) return;

        Optional<List<CachedVirtualCrate>> optList = WebAPIAPI.runOnMain(() -> {
            List<CachedVirtualCrate> crates = new ArrayList<>();
            for (VirtualCrate crate : plugin.getCrateUtilities().crateTypes.values()) {
                crates.add(new CachedVirtualCrate(crate));
            }
            return crates;
        });

        data.addJson("ok", optList.isPresent(), false);
        data.addJson("crates", optList.orElse(null), data.getQueryParam("details").isPresent());
    }

    @Endpoint(method = HttpMethod.GET, path = "crate/:id", perm = "one")
    public void getCrate(IServletData data, String id) {
        HuskyCrates plugin = getHuskyPlugin(data);
        if (plugin == null) return;

        Optional<CachedVirtualCrate> optRes = WebAPIAPI.runOnMain(() -> {
            VirtualCrate crate = plugin.crateUtilities.getVirtualCrate(id);
            if (crate == null) {
                data.sendError(HttpServletResponse.SC_NOT_FOUND, "Crate not found");
                return null;
            }

            return new CachedVirtualCrate(crate);
        });

        data.addJson("ok", optRes.isPresent(), false);
        data.addJson("crate", optRes.orElse(null), true);
    }
}
