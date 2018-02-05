package valandur.webapi.servlet;

import com.flowpowered.math.vector.Vector3d;
import io.swagger.annotations.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.entity.ICachedEntity;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.ExplicitDetails;
import valandur.webapi.api.servlet.Permission;
import valandur.webapi.cache.entity.CachedEntity;
import valandur.webapi.serialize.deserialize.DamageRequest;
import valandur.webapi.serialize.deserialize.ExecuteMethodRequest;
import valandur.webapi.util.Util;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
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
    @ApiOperation(value = "Get entities", notes = "Get a list of all entities on the server (in all worlds).")
    public Collection<ICachedEntity> getEntities(
            @QueryParam("world") @ApiParam("The world to filter the entities by") ICachedWorld world,
            @QueryParam("type") @ApiParam("The type id of the entities to filter by") String typeId,
            @QueryParam("limit") @ApiParam("The maximum amount of entities returned") int limit)
            throws InternalServerErrorException {
        Predicate<Entity> filter = e -> typeId == null || e.getType().getId().equalsIgnoreCase(typeId);

        if (world != null) {
            return cacheService.getEntities(world, filter, limit);
        }

        return cacheService.getEntities(filter, limit);
    }

    @GET
    @Path("/{entity}")
    @Permission("one")
    @ApiOperation(value = "Get entity", notes = "Get detailed information about an entity.")
    public ICachedEntity getEntity(
            @PathParam("entity") @ApiParam("The uuid of the entity") UUID uuid)
            throws NotFoundException {
        Optional<ICachedEntity> optEntity = WebAPI.getCacheService().getEntity(uuid);
        if (!optEntity.isPresent()) {
            throw new NotFoundException("Entity with UUID '" + uuid + "' could not be found");
        }

        return optEntity.get();
    }

    @PUT
    @Path("/{entity}")
    @Permission("change")
    @ApiOperation(value = "Change an entity", notes = "Update the properties of an existing entity.")
    public ICachedEntity updateEntity(
            @PathParam("entity") @ApiParam("The uuid of the entity") UUID uuid,
            UpdateEntityRequest req)
            throws InternalServerErrorException, NotFoundException, BadRequestException {
        Optional<ICachedEntity> optEntity = WebAPI.getCacheService().getEntity(uuid);
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
                if (dmgReq.getDamageType().isPresent())
                    builder.type(dmgReq.getDamageType().get());

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
    @ApiOperation(value = "Spawn an entity", notes = "Creates & Spawns a new entity with the specified properties.")
    public ICachedEntity createEntity(CreateEntityRequest req)
            throws BadRequestException {
        Optional<ICachedWorld> optWorld = req.getWorld();
        if (!optWorld.isPresent()) {
            throw new BadRequestException("No valid world provided");
        }

        Optional<EntityType> optEntType = req.getEntityType();
        if (!optEntType.isPresent()) {
            throw new BadRequestException("No valid entity type provided");
        }

        if (req.getPosition() == null) {
            throw new BadRequestException("No valid position provided");
        }

        return WebAPI.runOnMain(() -> {
            Optional<World> optLive = optWorld.get().getLive();
            if (!optLive.isPresent())
                throw new InternalServerErrorException("Could not get live entity");

            World w = optLive.get();
            Entity e = w.createEntity(optEntType.get(), req.getPosition());

            if (w.spawnEntity(e)) {
                return new CachedEntity(e);
            } else {
                e.remove();
                throw new InternalServerErrorException("Could not spawn entity");
            }
        });
    }

    @POST
    @Path("/{entity}/method")
    @Permission("method")
    @ApiOperation(value = "Execute a method",
            notes = "Provides direct access to the underlaying entity object and can execute any method on it.")
    public Object executeMethod(
            @PathParam("entity") @ApiParam("The uuid of the entity") UUID uuid,
            ExecuteMethodRequest req)
            throws NotFoundException, BadRequestException, InternalServerErrorException {
        Optional<ICachedEntity> optEntity = WebAPI.getCacheService().getEntity(uuid);
        if (!optEntity.isPresent()) {
            throw new NotFoundException("Entity with UUID '" + uuid + "' could not be found");
        }

        if (req.getMethod() == null || req.getMethod().isEmpty()) {
            throw new BadRequestException("Method must be specified");
        }

        String mName = req.getMethod();
        Optional<Tuple<Class[], Object[]>> params = Util.parseParams(req.getParameters());

        if (!params.isPresent()) {
            throw new BadRequestException("Invalid parameters");
        }

        return cacheService.executeMethod(optEntity.get(), mName, params.get().getFirst(), params.get().getSecond());
    }

    @DELETE
    @Path("/{entity}")
    @Permission("delete")
    @ApiOperation(value = "Destroy an entity", notes = "Destroys an entity.")
    public ICachedEntity removeEntity(
            @PathParam("entity") @ApiParam("The uuid of the entity") UUID uuid)
            throws NotFoundException, InternalServerErrorException {
        Optional<ICachedEntity> optEntity = WebAPI.getCacheService().getEntity(uuid);
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


    @ApiModel("Create Entity RequesT")
    public static class CreateEntityRequest {

        private String world;
        @ApiModelProperty(dataType = "string", value = "The world that the entity will be spawned in", required = true)
        public Optional<ICachedWorld> getWorld() {
            return WebAPI.getCacheService().getWorld(world);
        }

        private Vector3d position;
        @ApiModelProperty(value = "The position at which the entity will be spawned", required = true)
        public Vector3d getPosition() {
            return position;
        }

        private String type;
        @ApiModelProperty(dataType = "string", value = "The type of entity that will be spawned", required = true)
        public Optional<EntityType> getEntityType() {
            Collection<EntityType> types = Sponge.getRegistry().getAllOf(EntityType.class);
            return types.stream().filter(g -> g.getId().equalsIgnoreCase(type) || g.getName().equalsIgnoreCase(type)).findAny();
        }
    }

    @ApiModel("Update Entity Request")
    public static class UpdateEntityRequest {

        private String world;
        @ApiModelProperty(dataType = "string", value = "The world that the entity will be moved to")
        public Optional<ICachedWorld> getWorld() {
            return WebAPI.getCacheService().getWorld(world);
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
}
