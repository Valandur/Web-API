package valandur.webapi.servlet;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import io.swagger.annotations.*;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.entity.CachedEntity;
import valandur.webapi.cache.misc.CachedCatalogType;
import valandur.webapi.cache.world.CachedWorld;
import valandur.webapi.serialize.objects.ExecuteMethodRequest;
import valandur.webapi.serialize.objects.ExecuteMethodResponse;
import valandur.webapi.servlet.base.BaseServlet;
import valandur.webapi.servlet.base.ExplicitDetails;
import valandur.webapi.servlet.base.Permission;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

@Path("entity")
@Api(tags = { "Entity" }, value = "List all entities and get detailed information about them.")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class EntityServlet extends BaseServlet {

    @GET
    @ExplicitDetails
    @Permission("list")
    @ApiOperation(value = "List entities", notes = "Get a list of all entities on the server (in all worlds).")
    public Collection<CachedEntity> listEntities(
            @QueryParam("world") @ApiParam("The world to filter the entities by") CachedWorld world,
            @QueryParam("type") @ApiParam("The type id of the entities to filter by") String typeId,
            @QueryParam("min") @ApiParam("The minimum coordinates at which the entity must be, min=x|y|z") Vector3i min,
            @QueryParam("max") @ApiParam("The maximum coordinates at which the entity must be, max=x|y|z") Vector3i max,
            @QueryParam("limit") @ApiParam("The maximum amount of entities returned") int limit) {
        Predicate<Entity> filter = e -> typeId == null || e.getType().getId().equalsIgnoreCase(typeId);

        return cacheService.getEntities(world, min, max, filter, limit);
    }

    @GET
    @Path("/{entity}")
    @Permission("one")
    @ApiOperation(
            value = "Get entity",
            notes = "Get detailed information about an entity.")
    public CachedEntity getEntity(
            @PathParam("entity") @ApiParam("The uuid of the entity") UUID uuid)
            throws NotFoundException {
        Optional<CachedEntity> optEntity = WebAPI.getCacheService().getEntity(uuid);
        if (!optEntity.isPresent()) {
            throw new NotFoundException("Entity with UUID '" + uuid + "' could not be found");
        }

        return optEntity.get();
    }

    @PUT
    @Path("/{entity}")
    @Permission("modify")
    @ApiOperation(
            value = "Modify an entity",
            notes = "Modify the properties of an existing entity.")
    public CachedEntity modifyEntity(
            @PathParam("entity") @ApiParam("The uuid of the entity") UUID uuid,
            UpdateEntityRequest req)
            throws NotFoundException, BadRequestException {

        if (req == null) {
            throw new BadRequestException("Request body is required");
        }

        Optional<CachedEntity> optEntity = WebAPI.getCacheService().getEntity(uuid);
        if (!optEntity.isPresent()) {
            throw new NotFoundException("Entity with UUID '" + uuid + "' could not be found");
        }

        return WebAPI.runOnMain(() -> {
            Optional<Entity> optLive = optEntity.get().getLive();
            if (!optLive.isPresent())
                throw new InternalServerErrorException("Could not get live entity");

            Entity live = optLive.get();

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

            if (req.getDamage() != null) {
                DamageRequest dmgReq = req.getDamage();
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
                if (!(live instanceof Carrier)) {
                    throw new BadRequestException("Entity does not have an inventory!");
                }

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

            return new CachedEntity(live);
        });
    }

    @POST
    @Permission("create")
    @ApiOperation(
            value = "Spawn an entity",
            response = CachedEntity.class,
            notes = "Creates & Spawns a new entity with the specified properties.")
    public Response createEntity(CreateEntityRequest req)
            throws BadRequestException, URISyntaxException {

        if (req == null) {
            throw new BadRequestException("Request body is required");
        }

        Optional<CachedWorld> optWorld = req.getWorld();
        if (!optWorld.isPresent()) {
            throw new BadRequestException("No valid world provided");
        }

        Optional<CachedCatalogType<EntityType>> optEntType = req.getType();
        if (!optEntType.isPresent()) {
            throw new BadRequestException("No valid entity type provided");
        }

        if (req.getPosition() == null) {
            throw new BadRequestException("No valid position provided");
        }

        CachedEntity ent = WebAPI.runOnMain(() -> {
            Optional<World> optLive = optWorld.get().getLive();
            if (!optLive.isPresent())
                throw new InternalServerErrorException("Could not get live entity");

            Optional<EntityType> optLiveType = optEntType.get().getLive(EntityType.class);
            if (!optLiveType.isPresent())
                throw new InternalServerErrorException("Could not get live entity type");

            World w = optLive.get();
            Entity e = w.createEntity(optLiveType.get(), req.getPosition());

            if (w.spawnEntity(e, Cause.source(WebAPI.getInstance()).build())) {
                return new CachedEntity(e);
            } else {
                e.remove();
                throw new InternalServerErrorException("Could not spawn entity");
            }
        });

        return Response.created(new URI(null, null, ent.getLink(), null)).entity(ent).build();
    }

    @POST
    @Path("/{entity}/method")
    @Permission("method")
    @ApiOperation(
            value = "Execute a method",
            notes = "Provides direct access to the underlaying entity object and can execute any method on it.")
    public ExecuteMethodResponse executeMethod(
            @PathParam("entity") @ApiParam("The uuid of the entity") UUID uuid,
            ExecuteMethodRequest req)
            throws NotFoundException, BadRequestException {

        if (req == null) {
            throw new BadRequestException("Request body is required");
        }

        Optional<CachedEntity> optEntity = WebAPI.getCacheService().getEntity(uuid);
        if (!optEntity.isPresent()) {
            throw new NotFoundException("Entity with UUID '" + uuid + "' could not be found");
        }

        if (req.getMethod() == null || req.getMethod().isEmpty()) {
            throw new BadRequestException("Method must be specified");
        }

        String mName = req.getMethod();
        Tuple<Class[], Object[]> params = req.getParsedParameters();
        Object res = cacheService.executeMethod(optEntity.get(), mName, params.getFirst(), params.getSecond());
        return new ExecuteMethodResponse(optEntity.get(), res);
    }

    @DELETE
    @Path("/{entity}")
    @Permission("delete")
    @ApiOperation(
            value = "Destroy an entity",
            notes = "Destroys an entity.")
    public CachedEntity removeEntity(
            @PathParam("entity") @ApiParam("The uuid of the entity") UUID uuid)
            throws NotFoundException {
        Optional<CachedEntity> optEntity = WebAPI.getCacheService().getEntity(uuid);
        if (!optEntity.isPresent()) {
            throw new NotFoundException("Entity with UUID '" + uuid + "' could not be found");
        }

        WebAPI.runOnMain(() -> {
            Optional<Entity> live = optEntity.get().getLive();
            if (!live.isPresent())
                throw new InternalServerErrorException("Could not get live entity");

            live.get().remove();
        });

        return optEntity.get();
    }


    @ApiModel("CreateEntityRequest")
        public static class CreateEntityRequest {

        private CachedWorld world;
        @ApiModelProperty(dataType = "string", value = "The world that the entity will be spawned in", required = true)
        public Optional<CachedWorld> getWorld() {
            return world != null ? Optional.of(world) : Optional.empty();
        }

        private Vector3d position;
        @ApiModelProperty(value = "The position where the entity is spawned", required = true)
        public Vector3d getPosition() {
            return position;
        }

        private CachedCatalogType<EntityType> type;
        @ApiModelProperty(dataType = "string", value = "The type of entity that will be spawned", required = true)
        public Optional<CachedCatalogType<EntityType>> getType() {
            return type != null ? Optional.of(type) : Optional.empty();
        }
    }

    @ApiModel("UpdateEntityRequest")
        public static class UpdateEntityRequest {

        private CachedWorld world;
        @ApiModelProperty(dataType = "string", value = "The world that the entity will be moved to")
        public Optional<CachedWorld> getWorld() {
            return world != null ? Optional.of(world) : Optional.empty();
        }

        private Vector3d position;
        @ApiModelProperty("The position that the entity will be moved to")
        public Vector3d getPosition() {
            return position;
        }

        private Vector3d velocity;
        @ApiModelProperty("The new speed of the entity")
        public Vector3d getVelocity() {
            return velocity;
        }

        private Vector3d rotation;
        @ApiModelProperty("The new rotation of the entity")
        public Vector3d getRotation() {
            return rotation;
        }

        private Vector3d scale;
        @ApiModelProperty("The new scale of the entity")
        public Vector3d getScale() {
            return scale;
        }

        private DamageRequest damage;
        @ApiModelProperty("The damage the entity will take")
        public DamageRequest getDamage() {
            return damage;
        }

        private List<ItemStack> inventory;
        @ApiModelProperty("The ItemStacks in the inventory of the entity")
        public List<ItemStack> getInventory() throws Exception {
            return inventory;
        }
        public boolean hasInventory() {
            return inventory != null;
        }
    }

    @ApiModel("DamageRequest")
        public static class DamageRequest {

        private Integer amount;
        @ApiModelProperty("The amount of damage that should be dealt to the entity")
        public Integer getAmount() {
            return amount;
        }

        private CachedCatalogType<DamageType> type;
        @ApiModelProperty(dataType = "string", value = "The type of damage that should be dealt")
        public Optional<CachedCatalogType<DamageType>> getType() {
            return type != null ? Optional.of(type) : Optional.empty();
        }
    }
}
