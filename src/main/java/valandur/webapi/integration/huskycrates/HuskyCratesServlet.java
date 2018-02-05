package valandur.webapi.integration.huskycrates;

import com.codehusky.huskycrates.HuskyCrates;
import com.codehusky.huskycrates.crate.VirtualCrate;
import com.codehusky.huskycrates.crate.config.CrateReward;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import valandur.webapi.api.WebAPIAPI;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Permission;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Path("husky-crates")
@Api(tags = { "Husky Crates" }, value =
        "Create, edit and delete the crate types on your server, without having to restart it.")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class HuskyCratesServlet extends BaseServlet {

    public static void onRegister() {
        WebAPIAPI.getJsonService().ifPresent(srv -> {
            srv.registerCache(VirtualCrate.class, CachedVirtualCrate.class);
            srv.registerCache(CrateReward.class, CachedCrateReward.class);
        });
    }


    private HuskyCrates getHuskyPlugin() {
        Optional<PluginContainer> optContainer = Sponge.getPluginManager().getPlugin("huskycrates");
        if (!optContainer.isPresent()) {
            throw new InternalServerErrorException("HuskyCrates plugin not found");
        }

        Optional<?> optPlugin = optContainer.get().getInstance();
        if (!optPlugin.isPresent()) {
            throw new InternalServerErrorException("HuskyCrates plugin instance not found");
        }

        return (HuskyCrates)optPlugin.get();
    }

    @GET
    @Path("/crate")
    @Permission({ "crate", "list" })
    @ApiOperation(value = "List crates", notes = "Get a list of all the crates on the server.")
    public Collection<CachedVirtualCrate> getCrates() {
        HuskyCrates plugin = getHuskyPlugin();

        Optional<List<CachedVirtualCrate>> optList = WebAPIAPI.runOnMain(() -> {
            List<CachedVirtualCrate> crates = new ArrayList<>();
            for (VirtualCrate crate : plugin.getCrateUtilities().crateTypes.values()) {
                crates.add(new CachedVirtualCrate(crate));
            }
            return crates;
        });

        return optList.orElse(null);
    }

    @GET
    @Path("/crate/{id}")
    @Permission({ "crate", "one" })
    @ApiOperation(value = "Get a crate", notes = "Get detailed information about a crate.")
    public CachedVirtualCrate getCrate(@PathParam("id") String id)
            throws NotFoundException, InternalServerErrorException {
        HuskyCrates plugin = getHuskyPlugin();

        Optional<CachedVirtualCrate> optRes = WebAPIAPI.runOnMain(() -> {
            VirtualCrate crate = plugin.crateUtilities.getVirtualCrate(id);
            if (crate == null) {
                throw new NotFoundException("Crate with id " + id + " not found");
            }

            return new CachedVirtualCrate(crate);
        });

        return optRes.orElse(null);
    }

    @POST
    @Path("/crate")
    @Permission({ "crate", "create "})
    @ApiOperation(value = "Create a crate", notes = "Creates a new crate.")
    public CachedVirtualCrate createCrate(CachedVirtualCrate req)
            throws InternalServerErrorException {
        HuskyCrates plugin = getHuskyPlugin();

        Optional<CachedVirtualCrate> optCrate = WebAPIAPI.runOnMain(() -> {
            try {
                saveCrate(plugin.crateConfig, req);
                plugin.crateUtilities.generateVirtualCrates(plugin.crateConfig);

                VirtualCrate crate = plugin.crateUtilities.getVirtualCrate(req.getId());
                if (crate == null) {
                    throw new InternalServerErrorException("Could not get crate after creating it");
                }

                return new CachedVirtualCrate(crate);
            } catch (IOException e) {
                throw new InternalServerErrorException(e.getMessage());
            }
        });

        return optCrate.orElse(null);
    }

    @PUT
    @Path("/crate/{id}")
    @Permission({ "crate", "change" })
    @ApiOperation(value = "Change a crate", notes = "Change a crate.")
    public CachedVirtualCrate changeCrate(@PathParam("id") String id, CachedVirtualCrate req)
            throws NotFoundException, InternalServerErrorException {
        HuskyCrates plugin = getHuskyPlugin();

        Optional<CachedVirtualCrate> optRes = WebAPIAPI.runOnMain(() -> {
            try {
                VirtualCrate oldCrate = plugin.crateUtilities.getVirtualCrate(id);
                if (oldCrate == null) {
                    throw new NotFoundException("Crate with id " + id + " not found");
                }

                req.setId(oldCrate.id);
                saveCrate(plugin.crateConfig, req);
                plugin.crateUtilities.generateVirtualCrates(plugin.crateConfig);

                VirtualCrate crate = plugin.crateUtilities.getVirtualCrate(req.getId());

                if (crate == null) {
                    throw new InternalServerErrorException("Could not get crate after modifying it");
                }

                return new CachedVirtualCrate(crate);
            } catch (IOException e) {
                throw new InternalServerErrorException(e.getMessage());
            }
        });

        return optRes.orElse(null);
    }

    @DELETE
    @Path("/crate/{id}")
    @Permission({ "crate", "delete" })
    @ApiOperation(value = "Delete a crate", notes = "Delete a crate.")
    public CachedVirtualCrate deleteCrate(@PathParam("id") String id)
            throws NotFoundException, InternalServerErrorException {
        HuskyCrates plugin = getHuskyPlugin();

        Optional<CachedVirtualCrate> optCrate = WebAPIAPI.runOnMain(() -> {
            VirtualCrate crate = plugin.crateUtilities.getVirtualCrate(id);
            if (crate == null) {
                throw new NotFoundException("Crate with id " + id + " not found");
            }

            try {
                ConfigurationNode node = node = plugin.crateConfig.load();
                node.getNode("crates").removeChild(crate.id);
                plugin.crateConfig.save(node);

                plugin.crateUtilities.generateVirtualCrates(plugin.crateConfig);

                return new CachedVirtualCrate(crate);
            } catch (IOException e) {
                throw new InternalServerErrorException(e.getMessage());
            }
        });

        return optCrate.orElse(null);
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
