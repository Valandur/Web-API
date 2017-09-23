package valandur.webapi.integration.huskycrates;

import com.codehusky.huskycrates.HuskyCrates;
import com.codehusky.huskycrates.crate.VirtualCrate;
import org.eclipse.jetty.http.HttpMethod;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import valandur.webapi.WebAPI;
import valandur.webapi.api.annotation.WebAPIEndpoint;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.servlet.WebAPIBaseServlet;
import valandur.webapi.json.JsonService;
import valandur.webapi.servlet.base.ServletData;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebAPIServlet(basePath = "husky")
public class HuskyCratesServlet extends WebAPIBaseServlet {

    public static void onRegister() {
        JsonService json = WebAPI.getJsonService();
        json.registerSerializer(CachedCrate.class, CachedCrateSerializer.class);
        json.registerSerializer(CachedCrateReward.class, CachedCrateRewardSerializer.class);
    }


    private HuskyCrates getHuskyPlugin(ServletData data) {
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

    @WebAPIEndpoint(method = HttpMethod.GET, path = "crate", perm = "list")
    public void getCrates(ServletData data) {
        HuskyCrates plugin = getHuskyPlugin(data);
        if (plugin == null) return;

        Optional<List<CachedCrate>> optList = WebAPI.runOnMain(() -> {
            List<CachedCrate> crates = new ArrayList<>();
            for (VirtualCrate crate : plugin.getCrateUtilities().crateTypes.values()) {
                crates.add(new CachedCrate(crate));
            }
            return crates;
        });

        data.addJson("ok", optList.isPresent(), false);
        data.addJson("crates", optList.orElse(null), data.getQueryParam("details").isPresent());
    }

    @WebAPIEndpoint(method = HttpMethod.GET, path = "crate/:id", perm = "one")
    public void getCrate(ServletData data, String id) {
        HuskyCrates plugin = getHuskyPlugin(data);
        if (plugin == null) return;

        Optional<CachedCrate> optRes = WebAPI.runOnMain(() -> {
            VirtualCrate crate = plugin.crateUtilities.getVirtualCrate(id);
            if (crate == null) {
                data.sendError(HttpServletResponse.SC_NOT_FOUND, "Crate not found");
                return null;
            }

            return new CachedCrate(crate);
        });

        data.addJson("ok", optRes.isPresent(), false);
        data.addJson("crate", optRes.orElse(null), true);
    }
}
