package valandur.webapi.servlet.entity;

import com.fasterxml.jackson.databind.JsonNode;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;
import valandur.webapi.permission.Permission;
import valandur.webapi.cache.entity.CachedEntity;
import valandur.webapi.cache.DataCache;
import valandur.webapi.misc.Util;
import valandur.webapi.servlet.ServletData;
import valandur.webapi.servlet.WebAPIServlet;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class EntityServlet extends WebAPIServlet {
    @Override
    @Permission(perm = "entity.get")
    protected void handleGet(ServletData data) {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            data.addJson("ok", true, false);
            data.addJson("entities", DataCache.getEntities(), data.getQueryPart("details").isPresent());
            return;
        }

        String uuid = paths[0];
        if (!Util.isValidUUID(uuid)) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid entity UUID");
            return;
        }

        Optional<CachedEntity> entity = DataCache.getEntity(UUID.fromString(uuid));
        if (!entity.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Entity with UUID '" + uuid + "' could not be found");
            return;
        }

        Optional<String> strFields = data.getQueryPart("fields");
        Optional<String> strMethods = data.getQueryPart("methods");
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

    @Override
    @Permission(perm = "entity.put")
    protected void handlePut(ServletData data) {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid entity UUID");
            return;
        }

        String uuid = paths[0];
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

    @Override
    @Permission(perm = "entity.post")
    protected void handlePost(ServletData data) {
        String[] paths = data.getPathParts();

        // A post directly to /api/entity creates a new entity
        if (paths.length < 1 || paths[0].isEmpty()) {
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
            return;
        }

        // A post to /api/entity/{uuid}/{method} is handled here
        String uuid = paths[0];
        if (uuid.split("-").length != 5) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid entity UUID");
            return;
        }

        Optional<CachedEntity> entity = DataCache.getEntity(UUID.fromString(uuid));
        if (!entity.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Entity with UUID '" + uuid + "' could not be found");
            return;
        }

        if (paths.length < 2 || paths[1].isEmpty()) {
            data.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Invalid method");
            return;
        }

        String method = paths[1].toLowerCase();
        final JsonNode reqJson = data.getRequestBody();

        switch (method) {
            case "method":
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
                break;

            default:
                data.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Invalid method");
        }
    }

    @Override
    @Permission(perm = "entity.delete")
    protected void handleDelete(ServletData data) {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid entity UUID");
            return;
        }

        String uuid = paths[0];
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
