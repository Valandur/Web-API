package valandur.webapi.servlet;

import io.swagger.annotations.*;
import org.spongepowered.api.data.manipulator.mutable.entity.ExperienceHolderData;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.player.ICachedPlayerFull;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.ExplicitDetails;
import valandur.webapi.api.servlet.Permission;
import valandur.webapi.serialize.deserialize.ExecuteMethodRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Path("player")
@Api(tags = { "Player" }, value = "List all players and get detailed information about them.")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class PlayerServlet extends BaseServlet {

    @GET
    @ExplicitDetails
    @Permission("list")
    @ApiOperation(
            value = "List players",
            notes = "Get a list of all the players on the server.")
    public Collection<ICachedPlayerFull> listPlayers() {
        return cacheService.getPlayers();
    }

    @GET
    @Path("/{player}")
    @Permission("one")
    @ApiOperation(
            value = "Get a player",
            notes = "Get detailed information about a player.")
    public ICachedPlayerFull getPlayer(
            @PathParam("player") @ApiParam("The uuid of the player") UUID uuid)
            throws NotFoundException {
        Optional<ICachedPlayerFull> optPlayer = WebAPI.getCacheService().getPlayer(uuid);
        if (!optPlayer.isPresent()) {
            throw new NotFoundException("Player with UUID '" + uuid + "' could not be found");
        }

        return optPlayer.get();
    }

    @PUT
    @Path("/{player}")
    @Permission("modify")
    @ApiOperation(
            value = "Modify a player",
            notes = "Modify the properties of an existing player.")
    public ICachedPlayerFull modifyPlayer(
            @PathParam("player") @ApiParam("The uuid of the player") UUID uuid,
            UpdatePlayerRequest req)
            throws NotFoundException {

        if (req == null) {
            throw new BadRequestException("Request body is required");
        }

        Optional<ICachedPlayerFull> optPlayer = WebAPI.getCacheService().getPlayer(uuid);
        if (!optPlayer.isPresent()) {
            throw new NotFoundException("Player with UUID '" + uuid + "' could not be found");
        }

        return WebAPI.runOnMain(() -> {
            Optional<Player> optLive = optPlayer.get().getLive();
            if (!optLive.isPresent())
                throw new InternalServerErrorException("Could not get live player");

            Player live = optLive.get();

            if (req.getWorld().isPresent()) {
                Optional<World> optWorld = req.getWorld().get().getLive();
                if (!optWorld.isPresent())
                    throw new InternalServerErrorException("Could not get live world");

                if (req.getPosition() != null) {
                    live.transferToWorld(optWorld.get(), req.getPosition());
                } else {
                    live.transferToWorld(optWorld.get());
                }
            } else if (req.getPosition() != null) {
                live.setLocation(new Location<>(live.getWorld(), req.getPosition()));
            }
            if (req.getVelocity() != null) {
                live.setVelocity(req.getVelocity());
            }
            if (req.getRotation() != null) {
                live.setRotation(req.getRotation());
            }

            if (req.getScale() != null) {
                live.setRotation(req.getScale());
            }

            if (req.getFoodLevel() != null) {
                live.getFoodData().foodLevel().set(req.getFoodLevel());
            }
            if (req.getExhaustion() != null) {
                live.getFoodData().exhaustion().set(req.getExhaustion());
            }
            if (req.getSaturation() != null) {
                live.getFoodData().saturation().set(req.getSaturation());
            }

            if (req.getTotalExperience() != null) {
                live.get(ExperienceHolderData.class).map(exp -> exp.totalExperience().set(req.getTotalExperience()));
            }
            if (req.getLevel() != null) {
                live.get(ExperienceHolderData.class).map(exp -> exp.level().set(req.getLevel()));
            }
            if (req.getExperienceSinceLevel() != null) {
                live.get(ExperienceHolderData.class).map(exp -> exp.experienceSinceLevel().set(req.getExperienceSinceLevel()));
            }

            if (req.getHealth() != null) {
                live.get(HealthData.class).map(h -> h.health().set(req.getHealth()));
            }
            if (req.getMaxHealth() != null) {
                live.get(HealthData.class).map(h -> h.maxHealth().set(req.getMaxHealth()));
            }

            if (req.getDamage() != null) {
                EntityServlet.DamageRequest dmgReq = req.getDamage();
                DamageSource.Builder builder = DamageSource.builder();
                if (dmgReq.getDamageType().isPresent())
                    builder.type(dmgReq.getDamageType().get());

                live.damage(req.getDamage().getAmount(), builder.build());
            }

            if (req.hasInventory()) {
                try {
                    Inventory inv = ((Carrier) live).getInventory();
                    inv.clear();
                    for (ItemStack stack : req.getInventory()) {
                        inv.offer(stack);
                    }
                } catch (Exception e) {
                    throw new InternalServerErrorException(e.getMessage());
                }
            }

            return cacheService.updatePlayer(live);
        });
    }

    @POST
    @Path("/{player}/method")
    @Permission("method")
    @ApiOperation(
            value = "Execute a method",
            notes = "Provides direct access to the underlaying player object and can execute any method on it.")
    public Object executeMethod(
            @PathParam("player") @ApiParam("The uuid of the player") UUID uuid,
            ExecuteMethodRequest req)
            throws NotFoundException, BadRequestException {

        if (req == null) {
            throw new BadRequestException("Request body is required");
        }

        Optional<ICachedPlayerFull> optPlayer = WebAPI.getCacheService().getPlayer(uuid);
        if (!optPlayer.isPresent()) {
            throw new NotFoundException("Player with UUID '" + uuid + "' could not be found");
        }

        if (req.getMethod() == null || req.getMethod().isEmpty()) {
            throw new BadRequestException("Method must be specified");
        }

        String mName = req.getMethod();
        Tuple<Class[], Object[]> params = req.getParsedParameters();
        return cacheService.executeMethod(optPlayer.get(), mName, params.getFirst(), params.getSecond());
    }


    @ApiModel("Update Player Request")
    public static class UpdatePlayerRequest extends EntityServlet.UpdateEntityRequest {

        private Integer foodLevel;
        @ApiModelProperty("The food level of the player")
        public Integer getFoodLevel() {
            return foodLevel;
        }

        private Double saturation;
        @ApiModelProperty("The saturation of the player")
        public Double getSaturation() {
            return saturation;
        }

        private Double exhaustion;
        @ApiModelProperty("The exhaustion of the player")
        public Double getExhaustion() {
            return exhaustion;
        }

        private Integer totalExperience;
        @ApiModelProperty("The total experience of the player")
        public Integer getTotalExperience() {
            return totalExperience;
        }

        private Integer level;
        @ApiModelProperty("The player level")
        public Integer getLevel() {
            return level;
        }

        private Integer experienceSinceLevel;
        @ApiModelProperty("The amount of experience gained since the last level")
        public Integer getExperienceSinceLevel() {
            return experienceSinceLevel;
        }

        private Double health;
        @ApiModelProperty("The current amount of health the player has")
        public Double getHealth() {
            return health;
        }

        private Double maxHealth;
        @ApiModelProperty("The maximum health of the player")
        public Double getMaxHealth() {
            return maxHealth;
        }
    }
}
