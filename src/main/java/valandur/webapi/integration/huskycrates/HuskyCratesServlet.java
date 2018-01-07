package valandur.webapi.integration.huskycrates;

import com.codehusky.huskycrates.HuskyCrates;
import com.codehusky.huskycrates.crate.VirtualCrate;
import com.codehusky.huskycrates.crate.config.CrateReward;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.eclipse.jetty.http.HttpMethod;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import valandur.webapi.api.WebAPIAPI;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Endpoint;
import valandur.webapi.api.servlet.IServletData;
import valandur.webapi.api.servlet.Servlet;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Servlet(basePath = "husky")
public class HuskyCratesServlet extends BaseServlet {

    private static final int FORMAT_VERSION = 1;


    public static void onRegister() {
        WebAPIAPI.getJsonService().ifPresent(srv -> {
            srv.registerCache(VirtualCrate.class, CachedVirtualCrate.class);
            srv.registerCache(CrateReward.class, CachedCrateReward.class);
        });
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

    @Endpoint(method = HttpMethod.GET, path = "crate", perm = "crate.list")
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

        data.addData("ok", optList.isPresent(), false);
        data.addData("crates", optList.orElse(null), data.getQueryParam("details").isPresent());
    }

    @Endpoint(method = HttpMethod.GET, path = "crate/:id", perm = "crate.one")
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

        data.addData("ok", optRes.isPresent(), false);
        data.addData("crate", optRes.orElse(null), true);
    }

    @Endpoint(method = HttpMethod.POST, path = "crate", perm = "crate.create")
    public void createCrate(IServletData data) {
        HuskyCrates plugin = getHuskyPlugin(data);
        if (plugin == null) return;

        Optional<CachedVirtualCrate> optReq = data.getRequestBody(CachedVirtualCrate.class);
        if (!optReq.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid crate data: " + data.getLastParseError().getMessage());
            return;
        }

        CachedVirtualCrate req = optReq.get();

        Optional<CachedVirtualCrate> optCrate = WebAPIAPI.runOnMain(() -> {
            try {
                saveCrate(plugin.crateConfig, req);
                plugin.crateUtilities.generateVirtualCrates(plugin.crateConfig);

                VirtualCrate crate = plugin.crateUtilities.getVirtualCrate(req.getId());
                if (crate == null)
                    return null;

                return new CachedVirtualCrate(crate);
            } catch (IOException e) {
                return null;
            }
        });

        data.addData("ok", optCrate.isPresent(), false);
        data.addData("crate", optCrate.orElse(null), true);
    }

    @Endpoint(method = HttpMethod.PUT, path = "crate/:id", perm = "crate.change")
    public void changeCrate(IServletData data, String id) {
        HuskyCrates plugin = getHuskyPlugin(data);
        if (plugin == null) return;

        Optional<CachedVirtualCrate> optReq = data.getRequestBody(CachedVirtualCrate.class);
        if (!optReq.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid crate data: " + data.getLastParseError().getMessage());
            return;
        }

        CachedVirtualCrate req = optReq.get();

        Optional<CachedVirtualCrate> optRes = WebAPIAPI.runOnMain(() -> {
            try {
                VirtualCrate oldCrate = plugin.crateUtilities.getVirtualCrate(id);
                if (oldCrate == null) {
                    data.sendError(HttpServletResponse.SC_NOT_FOUND, "Crate not found");
                    return null;
                }

                req.setId(oldCrate.id);
                saveCrate(plugin.crateConfig, req);
                plugin.crateUtilities.generateVirtualCrates(plugin.crateConfig);

                VirtualCrate crate = plugin.crateUtilities.getVirtualCrate(req.getId());
                if (crate == null)
                    return null;

                return new CachedVirtualCrate(crate);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });

        data.addData("ok", optRes.isPresent(), false);
        data.addData("crate", optRes.orElse(null), true);
    }

    @Endpoint(method = HttpMethod.DELETE, path = "crate/:id", perm = "crate.delete")
    public void deleteCrate(IServletData data, String id) {
        HuskyCrates plugin = getHuskyPlugin(data);
        if (plugin == null) return;

        Optional<CachedVirtualCrate> optCrate = WebAPIAPI.runOnMain(() -> {
            VirtualCrate crate = plugin.crateUtilities.getVirtualCrate(id);
            if (crate == null) {
                data.sendError(HttpServletResponse.SC_NOT_FOUND, "Crate not found");
                return null;
            }

            try {
                ConfigurationNode node = node = plugin.crateConfig.load();
                node.getNode("crates").removeChild(crate.id);
                plugin.crateConfig.save(node);

                plugin.crateUtilities.generateVirtualCrates(plugin.crateConfig);

                return new CachedVirtualCrate(crate);
            } catch (IOException e) {
                return null;
            }
        });

        data.addData("ok", optCrate.isPresent(), false);
        data.addData("crate", optCrate.orElse(null), true);
    }

    private void saveCrate(ConfigurationLoader loader, CachedVirtualCrate crate) throws IOException {
        ConfigurationNode root = loader.load();
        ConfigurationNode node = root.getNode("crates", crate.getId());

        node.getNode("name").setValue(crate.getName());
        node.getNode("type").setValue(crate.getType());
        node.getNode("options", "freeCrate").setValue(crate.isFree());
        node.getNode("options", "freeCrateDelay").setValue(crate.getFreeDelay());
        node.getNode("options", "scrambleRewards").setValue(crate.isScrambleRewards());

        node.removeChild("items");

        if (crate.getRewards() != null) {
            ConfigurationNode itemsNode = node.getNode("items");
            for (CachedCrateReward reward : crate.getRewards()) {
                ConfigurationNode rewardNode = itemsNode.getAppendedNode();
                rewardNode.getNode("formatversion").setValue(FORMAT_VERSION);
                rewardNode.getNode("name").setValue(reward.getName());
                if (reward.getDisplayItem() != null)
                    rewardNode.getNode("id").setValue(reward.getDisplayItem().getItem().getId());
                else
                    rewardNode.getNode("id").setValue("minecraft:dirt");
                rewardNode.getNode("count").setValue(1);

                ConfigurationNode huskyNode = rewardNode.getNode("huskydata");
                huskyNode.getNode("weight").setValue(reward.getChance());
                huskyNode.removeChild("rewards");

                ConfigurationNode rewardsNode = huskyNode.getNode("rewards");
                for (CrateRewardObject object : reward.getObjects()) {
                    object.saveToNode(rewardsNode.getAppendedNode());
                }
            }
        }

        loader.save(root);
    }
}
