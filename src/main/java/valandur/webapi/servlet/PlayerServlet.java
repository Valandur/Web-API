package valandur.webapi.servlet;

import io.swagger.annotations.*;
import org.spongepowered.api.data.manipulator.mutable.entity.ExperienceHolderData;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperation;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.misc.CachedCatalogType;
import valandur.webapi.cache.player.CachedPlayer;
import valandur.webapi.serialize.objects.ExecuteMethodRequest;
import valandur.webapi.serialize.objects.ExecuteMethodResponse;
import valandur.webapi.servlet.base.BaseServlet;
import valandur.webapi.servlet.base.ExplicitDetails;
import valandur.webapi.servlet.base.Permission;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Optional;

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
    public Collection<CachedPlayer> listPlayers() {
        return cacheService.getPlayers();
    }

    @GET
    @Path("/{player}")
    @Permission("one")
    @ApiOperation(
            value = "Get a player",
            notes = "Get detailed information about a player.")
    public CachedPlayer getPlayer(
            @PathParam("player") @ApiParam("The uuid of the player") CachedPlayer player)
            throws NotFoundException {
        return player;
    }

    @PUT
    @Path("/{player}")
    @Permission("modify")
    @ApiOperation(
            value = "Modify a player",
            notes = "Modify the properties of an existing player.")
    public CachedPlayer modifyPlayer(
            @PathParam("player") @ApiParam("The uuid of the player") CachedPlayer player,
            UpdatePlayerRequest req)
            throws NotFoundException {

        if (req == null) {
            throw new BadRequestException("Request body is required");
        }

        return WebAPI.runOnMain(() -> {
            Optional<Player> optLive = player.getLive();
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
                live.offer(live.getFoodData().foodLevel().set(req.getFoodLevel()));
            }
            if (req.getExhaustion() != null) {
                live.offer(live.getFoodData().exhaustion().set(req.getExhaustion()));
            }
            if (req.getSaturation() != null) {
                live.offer(live.getFoodData().saturation().set(req.getSaturation()));
            }

            if (req.getTotalExperience() != null) {
                live.get(ExperienceHolderData.class).ifPresent(exp ->
                        live.offer(exp.totalExperience().set(req.getTotalExperience())));
            }
            if (req.getLevel() != null) {
                live.get(ExperienceHolderData.class).ifPresent(exp ->
                        live.offer(exp.level().set(req.getLevel())));
            }
            if (req.getExperienceSinceLevel() != null) {
                live.get(ExperienceHolderData.class).ifPresent(exp ->
                        live.offer(exp.experienceSinceLevel().set(req.getExperienceSinceLevel())));
            }

            if (req.getHealth() != null) {
                live.offer(live.getHealthData().health().set(req.getHealth()));
            }
            if (req.getMaxHealth() != null) {
                live.offer(live.getHealthData().maxHealth().set(req.getMaxHealth()));
            }

            if (req.getGameMode() != null) {
                Optional<GameMode> optGm = req.getGameMode().getLive(GameMode.class);
                if (!optGm.isPresent())
                    throw new InternalServerErrorException("Could not get live game mode");

                live.offer(live.gameMode().set(optGm.get()));
            }

            if (req.getDamage() != null) {
                EntityServlet.DamageRequest dmgReq = req.getDamage();
                DamageSource.Builder builder = DamageSource.builder();

                if (dmgReq.getType().isPresent()) {
                    Optional<DamageType> optDmgType = dmgReq.getType().get().getLive(DamageType.class);
                    if (!optDmgType.isPresent())
                        throw new InternalServerErrorException("Could not get live damage type");

                    builder.type(optDmgType.get());
                }

                live.damage(req.getDamage().getAmount(), builder.build());
            }

            if (req.hasInventory()) {
                try {
                    Inventory inv = live.getInventory();
                    for (EntityServlet.SlotRequest slotReq : req.getInventory()) {
                        for (Inventory slot : inv.slots()) {
                            Optional<SlotIndex> optIndex = slot.getInventoryProperty(SlotIndex.class);
                            if (!optIndex.isPresent() || !slotReq.getSlotIndex().equals(optIndex.get().getValue())) {
                                continue;
                            }
                            if (slotReq.getStack().isPresent()) {
                                slot.set(slotReq.getStack().get());
                            } else {
                                slot.clear();
                            }
                        }
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
            notes = "Provides direct access to the underlying player object and can execute any method on it.")
    public ExecuteMethodResponse executeMethod(
            @PathParam("player") @ApiParam("The uuid of the player") CachedPlayer player,
            ExecuteMethodRequest req)
            throws NotFoundException, BadRequestException {

        if (req == null) {
            throw new BadRequestException("Request body is required");
        }

        if (req.getMethod() == null || req.getMethod().isEmpty()) {
            throw new BadRequestException("Method must be specified");
        }

        String mName = req.getMethod();
        Tuple<Class[], Object[]> params = req.getParsedParameters();
        Object res = cacheService.executeMethod(player, mName, params.getFirst(), params.getSecond());
        return new ExecuteMethodResponse(player, res);
    }


    @ApiModel("UpdatePlayerRequest")
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

        private CachedCatalogType<GameMode> gameMode;
        @ApiModelProperty(dataType = "string", value = "The game mode of the player")
        public CachedCatalogType<GameMode> getGameMode() {
            return gameMode;
        }
    }
}
