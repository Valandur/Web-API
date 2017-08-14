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
import valandur.webapi.servlet.ServletData;

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

    @WebAPIEndpoint(method = HttpMethod.GET, path = "crate", perm = "husky")
    public void getCrates(ServletData data) {
        Optional<PluginContainer> optContainer = Sponge.getPluginManager().getPlugin("huskycrates");
        if (!optContainer.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "HuskyCrates plugin not found");
            return;
        }

        Optional<?> optPlugin = optContainer.get().getInstance();
        if (!optPlugin.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "HuskyCrates plugin instance not found");
            return;
        }

        List<CachedCrate> crates = new ArrayList<>();
        HuskyCrates plugin = (HuskyCrates)optPlugin.get();
        for (VirtualCrate crate : plugin.getCrateUtilities().crateTypes.values()) {
            crates.add(new CachedCrate(crate));
        }

        data.addJson("ok", true, false);
        data.addJson("crates", crates, data.getQueryParam("details").isPresent());
    }
}
