package valandur.webapi.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.jetty.http.HttpMethod;
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
import valandur.webapi.api.servlet.Endpoint;
import valandur.webapi.api.servlet.Servlet;
import valandur.webapi.cache.entity.CachedEntity;
import valandur.webapi.serialize.request.entity.CreateEntityRequest;
import valandur.webapi.serialize.request.entity.UpdateEntityRequest;
import valandur.webapi.serialize.request.misc.DamageRequest;
import valandur.webapi.servlet.base.ServletData;
import valandur.webapi.util.Util;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

@Servlet(basePath = "entity")
public class EntityServlet extends BaseServlet {

    @Endpoint(method = HttpMethod.GET, path = "/", perm = "list")
    public void getEntities(ServletData data) {
        Optional<String> worldUuid = data.getQueryParam("world");
        Optional<String> typeId = data.getQueryParam("type");
        Optional<String> limitString = data.getQueryParam("limit");
        Predicate<Entity> filter = e -> !typeId.isPresent() || e.getType().getId().equalsIgnoreCase(typeId.get());
        int limit = Integer.parseInt(limitString.orElse("0"));

        if (worldUuid.isPresent()) {
            Optional<ICachedWorld> world = cacheService.getWorld(worldUuid.get());
            if (!world.isPresent()) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "World with UUID '" + worldUuid + "' could not be found");
                return;
            }

            Optional<Collection<ICachedEntity>> entities = cacheService.getEntities(world.get(), filter, limit);
            if (!entities.isPresent()) {
                data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not get entities");
                return;
            }

            data.addData("ok", true, false);
            data.addData("entities", entities.get(), data.getQueryParam("details").isPresent());
            return;
        }

        Optional<Collection<ICachedEntity>> coll = cacheService.getEntities(filter, limit);
        if (!coll.isPresent()) {
            data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not get entities");
            return;
        }

        data.addData("ok", true, false);
        data.addData("entities", coll.get(), data.getQueryParam("details").isPresent());
    }

    @Endpoint(method = HttpMethod.GET, path = "/:entity", perm = "one")
    public void getEntity(ServletData data, CachedEntity entity) {
        Optional<String> strFields = data.getQueryParam("fields");
        Optional<String> strMethods = data.getQueryParam("methods");
        if (strFields.isPresent() || strMethods.isPresent()) {
            String[] fields = strFields.map(s -> s.split(",")).orElse(new String[]{});
            String[] methods = strMethods.map(s -> s.split(",")).orElse(new String[]{});
            Tuple extra = cacheService.getExtraData(entity, data.responseIsXml(), fields, methods);
            data.addData("fields", extra.getFirst(), true);
            data.addData("methods", extra.getSecond(), true);
        }

        data.addData("ok", true, false);
        data.addData("entity", entity, true);
    }

    @Endpoint(method = HttpMethod.PUT, path = "/:entity", perm = "change")
    public void updateEntity(ServletData data, CachedEntity entity) {
        Optional<UpdateEntityRequest> optReq = data.getRequestBody(UpdateEntityRequest.class);
        if (!optReq.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid entity data: " + data.getLastParseError().getMessage());
            return;
        }

        final UpdateEntityRequest req = optReq.get();

        Optional<ICachedEntity> resEntity = WebAPI.runOnMain(() -> {
            Optional<Entity> optLive = entity.getLive();
            if (!optLive.isPresent())
                return null;

            Entity live = optLive.get();

            if (req.getWorld().isPresent()) {
                Optional<World> optWorld = req.getWorld().get().getLive();
                if (!optWorld.isPresent())
                    return null;

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
                    data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Entity does not have an inventory!");
                    return null;
                }

                try {
                    Inventory inv = ((Carrier) live).getInventory();
                    inv.clear();
                    for (ItemStack stack : req.getInventory()) {
                        inv.offer(stack);
                    }
                } catch (Exception e) {
                    return null;
                }
            }

            return new CachedEntity(live);
        });

        data.addData("ok", resEntity.isPresent(), false);
        data.addData("entity", resEntity.orElse(null), true);
    }

    @Endpoint(method = HttpMethod.POST, path = "/", perm = "create")
    public void createEntity(ServletData data) {
        Optional<CreateEntityRequest> optReq = data.getRequestBody(CreateEntityRequest.class);
        if (!optReq.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid entity data: " + data.getLastParseError().getMessage());
            return;
        }

        CreateEntityRequest req = optReq.get();

        Optional<ICachedWorld> optWorld = req.getWorld();
        if (!optWorld.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "No valid world provided");
            return;
        }

        Optional<EntityType> optEntType = req.getEntityType();
        if (!optEntType.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "No valid entity type provided");
            return;
        }

        if (req.getPosition() == null) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "No valid position provided");
            return;
        }

        Optional<ICachedEntity> resEntity = WebAPI.runOnMain(() -> {
            Optional<World> optLive = optWorld.get().getLive();
            if (!optLive.isPresent())
                return null;

            World w = optLive.get();
            Entity e = w.createEntity(optEntType.get(), req.getPosition());

            if (w.spawnEntity(e)) {
                return new CachedEntity(e);
            } else {
                e.remove();
                return null;
            }
        });

        if (!resEntity.isPresent()) {
            data.addData("ok", false, false);
            return;
        }

        data.setStatus(HttpServletResponse.SC_CREATED);
        data.setHeader("Location", resEntity.get().getLink());
        data.addData("ok", true, false);
        data.addData("entity", resEntity.get(), true);
    }

    @Endpoint(method = HttpMethod.POST, path = "/:entity/method", perm = "method")
    public void executeMethod(ServletData data, CachedEntity entity) {
        final JsonNode reqJson = data.getRequestBody();
        if (!reqJson.has("method")) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request must define the 'method' property");
            return;
        }

        String mName = reqJson.get("method").asText();
        Optional<Tuple<Class[], Object[]>> params = Util.parseParams(reqJson.get("params"));

        if (!params.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters");
            return;
        }

        Optional<Object> res = cacheService.executeMethod(entity, mName, params.get().getFirst(), params.get().getSecond());
        if (!res.isPresent()) {
            data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not get entity");
            return;
        }

        data.addData("ok", true, false);
        data.addData("entity", entity, true);
        data.addData("result", res.get(), true);
    }

    @Endpoint(method = HttpMethod.DELETE, path = "/:entity", perm = "delete")
    public void removeEntity(ServletData data, CachedEntity entity) {
        Optional<Boolean> deleted = WebAPI.runOnMain(() -> {
            Optional<Entity> live = entity.getLive();
            if (!live.isPresent())
                return false;

            live.get().remove();
            return true;
        });

        if (!deleted.isPresent() || !deleted.get()) {
            data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not delete entity " + entity.getUUID());
            return;
        }

        data.addData("ok", true, false);
        data.addData("entity", entity, true);
    }
}
