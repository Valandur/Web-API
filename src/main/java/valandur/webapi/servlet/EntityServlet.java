package valandur.webapi.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.jetty.http.HttpMethod;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;
import valandur.webapi.api.servlet.Endpoint;
import valandur.webapi.api.servlet.Servlet;
import valandur.webapi.api.cache.entity.ICachedEntity;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.cache.entity.CachedEntity;
import valandur.webapi.servlet.request.entity.CreateEntityRequest;
import valandur.webapi.servlet.request.entity.UpdateEntityRequest;
import valandur.webapi.api.json.request.misc.DamageRequest;
import valandur.webapi.servlet.base.ServletData;
import valandur.webapi.util.Util;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Servlet(basePath = "entity")
public class EntityServlet extends BaseServlet {

    @Endpoint(method = HttpMethod.GET, path = "/", perm = "list")
    public void getEntities(ServletData data) {
        data.addJson("ok", true, false);
        data.addJson("entities", cacheService.getEntities(), data.getQueryParam("details").isPresent());
    }

    @Endpoint(method = HttpMethod.GET, path = "/:entity", perm = "one")
    public void getEntity(ServletData data, CachedEntity entity) {
        Optional<String> strFields = data.getQueryParam("fields");
        Optional<String> strMethods = data.getQueryParam("methods");
        if (strFields.isPresent() || strMethods.isPresent()) {
            String[] fields = strFields.map(s -> s.split(",")).orElse(new String[]{});
            String[] methods = strMethods.map(s -> s.split(",")).orElse(new String[]{});
            Tuple extra = cacheService.getExtraData(entity, fields, methods);
            data.addJson("fields", extra.getFirst(), true);
            data.addJson("methods", extra.getSecond(), true);
        }

        data.addJson("ok", true, false);
        data.addJson("entity", entity, true);
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
            Optional<?> optLive = entity.getLive();
            if (!optLive.isPresent())
                return null;

            Entity live = (Entity)optLive.get();

            if (req.getWorld().isPresent()) {
                Optional<?> optWorld = req.getWorld().get().getLive();
                if (!optWorld.isPresent())
                    return null;

                if (req.getPosition() != null) {
                    live.transferToWorld((World)optWorld.get(), req.getPosition());
                } else {
                    live.transferToWorld((World)optWorld.get());
                }
            } else if (req.getPosition() != null) {
                live.setLocation(new Location<World>(live.getWorld(), req.getPosition()));
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
                    for (ItemStackSnapshot stack : req.getInventory()) {
                        inv.offer(stack.createStack());
                    }
                } catch (Exception e) {
                    return null;
                }
            }

            return cacheService.updateEntity(live);
        });

        data.addJson("ok", resEntity.isPresent(), false);
        data.addJson("entity", resEntity.orElse(null), true);
    }

    @Endpoint(method = HttpMethod.POST, path = "/", perm = "create")
    public void createEntity(ServletData data) {
        Optional<CreateEntityRequest> optReq = data.getRequestBody(CreateEntityRequest.class);
        if (!optReq.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid entity data: " + data.getLastParseError().getMessage());
            return;
        }

        CreateEntityRequest req = optReq.get();

        if (!req.getWorld().isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "No valid world provided");
            return;
        }

        if (!req.getEntityType().isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "No valid entity type provided");
            return;
        }

        if (req.getPosition() == null) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "No valid position provided");
            return;
        }

        Optional<Entity> resEntity = WebAPI.runOnMain(() -> {
            Optional<?> optWorld = req.getWorld().get().getLive();
            if (!optWorld.isPresent())
                return null;

            World w = (World)optWorld.get();
            Entity e = w.createEntity(req.getEntityType().get(), req.getPosition());

            if (w.spawnEntity(e, Cause.source(WebAPI.getContainer()).build())) {
                return e;
            } else {
                e.remove();
                return null;
            }
        });

        if (!resEntity.isPresent()) {
            data.addJson("ok", false, false);
            return;
        }

        ICachedEntity entity = cacheService.updateEntity(resEntity.get());

        data.setStatus(HttpServletResponse.SC_CREATED);
        data.setHeader("Location", entity.getLink());
        data.addJson("ok", true, false);
        data.addJson("entity", entity, true);
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

        data.addJson("ok", true, false);
        data.addJson("entity", entity, true);
        data.addJson("result", res.get(), true);
    }

    @Endpoint(method = HttpMethod.DELETE, path = "/:entity", perm = "delete")
    public void removeEntity(ServletData data, CachedEntity entity) {
        Optional<Boolean> deleted = WebAPI.runOnMain(() -> {
            Optional<?> live = entity.getLive();
            if (!live.isPresent())
                return false;

            Entity e = (Entity)live.get();
            e.remove();
            return true;
        });

        if (!deleted.isPresent() || !deleted.get()) {
            data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not delete entity " + entity.getUUID());
            return;
        }

        cacheService.removeEntity(entity.getUUID());

        data.addJson("ok", true, false);
        data.addJson("entity", entity, true);
    }
}
