package valandur.webapi.servlet.entity;

import com.fasterxml.jackson.databind.JsonNode;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;
import valandur.webapi.annotation.WebAPISpec;
import valandur.webapi.cache.DataCache;
import valandur.webapi.cache.entity.CachedEntity;
import valandur.webapi.misc.Util;
import valandur.webapi.servlet.ServletData;
import valandur.webapi.servlet.WebAPIServlet;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

public class EntityServlet extends WebAPIServlet {

    @WebAPISpec(method = "GET", path = "/", perm = "entity.get")
    public void getEntities(ServletData data) {
        data.addJson("ok", true, false);
        data.addJson("entities", DataCache.getEntities(), data.getQueryParam("details").isPresent());
    }

    @WebAPISpec(method = "GET", path = "/:entity", perm = "entity.get")
    public void getEntity(ServletData data) {
        String uuid = data.getPathParam("entity");
        if (!Util.isValidUUID(uuid)) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid entity UUID");
            return;
        }

        Optional<CachedEntity> entity = DataCache.getEntity(UUID.fromString(uuid));
        if (!entity.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Entity with UUID '" + uuid + "' could not be found");
            return;
        }

        Optional<String> strFields = data.getQueryParam("fields");
        Optional<String> strMethods = data.getQueryParam("methods");
        if (strFields.isPresent() || strMethods.isPresent()) {
            String[] fields = strFields.map(s -> s.split(",")).orElse(new String[]{});
            String[] methods = strMethods.map(s -> s.split(",")).orElse(new String[]{});
            Tuple extra = DataCache.getExtraData(entity.get(), fields, methods);
            data.addJson("fields", extra.getFirst(), true);
            data.addJson("methods", extra.getSecond(), true);
        }

        data.addJson("ok", true, false);
        data.addJson("entity", entity.get(), true);
    }

    @WebAPISpec(method = "PUT", path = "/:entity", perm = "entity.put")
    public void updateEntity(ServletData data) {
        String uuid = data.getPathParam("entity");
        if (!Util.isValidUUID(uuid)) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid entity UUID");
            return;
        }

        Optional<CachedEntity> entity = DataCache.getEntity(UUID.fromString(uuid));
        if (!entity.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Entity with UUID '" + uuid + "' could not be found");
            return;
        }

        Optional<UpdateEntityRequest> optReq = data.getRequestBody(UpdateEntityRequest.class);
        if (!optReq.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid entity data: " + data.getLastParseError().getMessage());
            return;
        }

        final UpdateEntityRequest req = optReq.get();

        Optional<CachedEntity> resEntity = WebAPI.runOnMain(() -> {
            Optional<?> optLive = entity.get().getLive();
            if (!optLive.isPresent())
                return null;

            Entity live = (Entity)optLive.get();

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
                UpdateEntityRequest.DamageRequest dmgReq = req.getDamage();
                DamageSource.Builder builder = DamageSource.builder();
                if (dmgReq.getDamageType().isPresent())
                    builder.type(dmgReq.getDamageType().get());

                live.damage(req.getDamage().getAmount(), builder.build());
            }

            return DataCache.updateEntity(live);
        });

        data.addJson("ok", resEntity.isPresent(), false);
        data.addJson("entity", resEntity.orElse(null), true);
    }

    @WebAPISpec(method = "POST", path = "/", perm = "entity.post")
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

            if (w.spawnEntity(e, Cause.source(WebAPI.getInstance()).build())) {
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

        CachedEntity entity = DataCache.updateEntity(resEntity.get());

        data.setStatus(HttpServletResponse.SC_CREATED);
        data.addJson("ok", true, false);
        data.addJson("entity", entity, true);
        data.setHeader("Location", entity.getLink());
    }

    @WebAPISpec(method = "POST", path = "/:entity/method", perm = "entity.post")
    public void executeMethod(ServletData data) {
        String uuid = data.getPathParam("entity");
        if (uuid.split("-").length != 5) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid entity UUID");
            return;
        }

        Optional<CachedEntity> entity = DataCache.getEntity(UUID.fromString(uuid));
        if (!entity.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Entity with UUID '" + uuid + "' could not be found");
            return;
        }

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

        Optional<Object> res = DataCache.executeMethod(entity.get(), mName, params.get().getFirst(), params.get().getSecond());
        if (!res.isPresent()) {
            data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not get entity");
            return;
        }

        data.addJson("ok", true, false);
        data.addJson("entity", entity.get(), true);
        data.addJson("result", res.get(), true);
    }

    @WebAPISpec(method = "DELETE", path = "/:entity", perm = "entity.delete")
    public void removeEntity(ServletData data) {
        String uuid = data.getPathParam("entity");
        if (uuid.split("-").length != 5) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid entity UUID");
            return;
        }

        Optional<CachedEntity> optEntity = DataCache.getEntity(UUID.fromString(uuid));
        if (!optEntity.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Entity with UUID '" + uuid + "' could not be found");
            return;
        }

        CachedEntity entity = optEntity.get();

        Optional<Boolean> deleted = WebAPI.runOnMain(() -> {
            Optional<?> live = entity.getLive();
            if (!live.isPresent())
                return false;

            Entity e = (Entity)live.get();
            e.remove();
            return true;
        });

        if (!deleted.isPresent() || !deleted.get()) {
            data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not delete entity " + uuid);
            return;
        }

        DataCache.removeEntity(entity.getUUID());

        data.addJson("ok", true, false);
        data.addJson("entity", entity, true);
    }
}
