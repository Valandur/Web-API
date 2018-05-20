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
import valandur.webapi.WebAPI;
import valandur.webapi.serialize.SerializeService;
import valandur.webapi.servlet.base.BaseServlet;
import valandur.webapi.servlet.base.Permission;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Path("husky-crates")
@Api(
        tags = { "Husky Crates" },
        value = "Create, edit and delete the crate types on your server, without having to restart it.")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class HuskyCratesServlet extends BaseServlet {

    public static void onRegister() {
        SerializeService srv = WebAPI.getSerializeService();
        srv.registerCache(VirtualCrate.class, CachedVirtualCrate.class);
        srv.registerCache(CrateReward.class, CachedCrateReward.class);
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
    @ApiOperation(
            value = "List crates",
            notes = "Get a list of all the crates on the server.")
    public Collection<CachedVirtualCrate> listCrates() {
        return WebAPI.runOnMain(() -> {
            HuskyCrates plugin = getHuskyPlugin();
            List<CachedVirtualCrate> crates = new ArrayList<>();
            for (VirtualCrate crate : plugin.getCrateUtilities().crateTypes.values()) {
                crates.add(new CachedVirtualCrate(crate));
            }
            return crates;
        });
    }

    @GET
    @Path("/crate/{id}")
    @Permission({ "crate", "one" })
    @ApiOperation(
            value = "Get a crate",
            notes = "Get detailed information about a crate.")
    public CachedVirtualCrate getCrate(@PathParam("id") String id)
            throws NotFoundException {
        return WebAPI.runOnMain(() -> {
            HuskyCrates plugin = getHuskyPlugin();
            VirtualCrate crate = plugin.crateUtilities.getVirtualCrate(id);
            if (crate == null) {
                throw new NotFoundException("Crate with id " + id + " not found");
            }

            return new CachedVirtualCrate(crate);
        });
    }

    @POST
    @Path("/crate")
    @Permission({ "crate", "create "})
    @ApiOperation(
            value = "Create a crate",
            response = CachedVirtualCrate.class,
            notes = "Creates a new crate.")
    public Response createCrate(CachedVirtualCrate req) throws URISyntaxException {

        if (req == null) {
            throw new BadRequestException("Request body is required");
        }

        CachedVirtualCrate crate = WebAPI.runOnMain(() -> {
            HuskyCrates plugin = getHuskyPlugin();
            saveCrate(plugin.crateConfig, req);
            plugin.crateUtilities.generateVirtualCrates(plugin.crateConfig);

            VirtualCrate vCrate = plugin.crateUtilities.getVirtualCrate(req.getId());
            if (vCrate == null) {
                throw new InternalServerErrorException("Could not get crate after creating it");
            }

            return new CachedVirtualCrate(vCrate);
        });

        return Response.created(new URI(null, null, crate.getLink(), null)).entity(crate).build();
    }

    @PUT
    @Path("/crate/{id}")
    @Permission({ "crate", "modify" })
    @ApiOperation(
            value = "Change a crate",
            notes = "Modify a crate.")
    public CachedVirtualCrate modifyCrate(@PathParam("id") String id, CachedVirtualCrate req)
            throws NotFoundException {

        if (req == null) {
            throw new BadRequestException("Request body is required");
        }

        return WebAPI.runOnMain(() -> {
            HuskyCrates plugin = getHuskyPlugin();
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
        });
    }

    @DELETE
    @Path("/crate/{id}")
    @Permission({ "crate", "delete" })
    @ApiOperation(
            value = "Delete a crate",
            notes = "Delete a crate.")
    public CachedVirtualCrate deleteCrate(@PathParam("id") String id)
            throws NotFoundException {

        return WebAPI.runOnMain(() -> {
            HuskyCrates plugin = getHuskyPlugin();
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
    }

    private void saveCrate(ConfigurationLoader loader, CachedVirtualCrate crate) {
        try {
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
                        rewardNode.getNode("id").setValue(reward.getDisplayItem().getType().getId());
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
        } catch (IOException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}
