package valandur.webapi.servlet.world;

import com.fasterxml.jackson.databind.JsonNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.*;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.storage.WorldProperties;
import valandur.webapi.WebAPI;
import valandur.webapi.permission.Permission;
import valandur.webapi.cache.world.CachedWorld;
import valandur.webapi.cache.DataCache;
import valandur.webapi.misc.Util;
import valandur.webapi.servlet.ServletData;
import valandur.webapi.servlet.WebAPIServlet;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class WorldServlet extends WebAPIServlet {
    @Override
    @Permission(perm = "world.get")
    protected void handleGet(ServletData data) {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            data.addJson("ok", true, false);
            data.addJson("worlds", DataCache.getWorlds(), data.getQueryPart("details").isPresent());
            return;
        }

        String uuid = paths[0];
        if (!Util.isValidUUID(uuid)) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid world UUID");
            return;
        }

        Optional<CachedWorld> world = DataCache.getWorld(UUID.fromString(uuid));
        if (!world.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "World with UUID '" + uuid + "' could not be found");
            return;
        }

        Optional<String> strFields = data.getQueryPart("fields");
        Optional<String> strMethods = data.getQueryPart("methods");
        if (strFields.isPresent() || strMethods.isPresent()) {
            String[] fields = strFields.map(s -> s.split(",")).orElse(new String[]{});
            String[] methods = strMethods.map(s -> s.split(",")).orElse(new String[]{});
            Tuple extra = DataCache.getExtraData(world.get(), fields, methods);
            data.addJson("fields", extra.getFirst(), true);
            data.addJson("methods", extra.getSecond(), true);
        }

        data.addJson("ok", true, false);
        data.addJson("world", world.get(), true);
    }

    @Override
    @Permission(perm = "world.put")
    protected void handlePut(ServletData data) {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid world UUID");
            return;
        }

        String uuid = paths[0];
        if (!Util.isValidUUID(uuid)) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid world UUID");
            return;
        }

        Optional<CachedWorld> world = DataCache.getWorld(UUID.fromString(uuid));
        if (!world.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "World with UUID '" + uuid + "' could not be found");
            return;
        }

        Optional<UpdateWorldRequest> optReq = data.getRequestBody(UpdateWorldRequest.class);
        if (!optReq.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid world data: " + data.getLastParseError().getMessage());
            return;
        }

        final UpdateWorldRequest req = optReq.get();

        Optional<CachedWorld> resWorld = WebAPI.runOnMain(() -> {
            Optional<?> optLive = world.get().getLive();
            if (!optLive.isPresent())
                return null;

            Object live = optLive.get();
            WorldProperties props = live instanceof World ? ((World) live).getProperties() : (WorldProperties) live;

            if (req.isLoaded() != null && req.isLoaded() != world.get().isLoaded()) {
                if (req.isLoaded()) {
                    Optional<World> newWorld = Sponge.getServer().loadWorld(props);
                    if (newWorld.isPresent()) {
                        live = newWorld.get();
                        props = newWorld.get().getProperties();
                    }
                } else {
                    Sponge.getServer().unloadWorld((World)live);
                    Optional<WorldProperties> optProps = Sponge.getServer().getUnloadedWorlds()
                            .stream().filter(w -> w.getUniqueId().equals(world.get().getUUID())).findAny();
                    if (optProps.isPresent()) {
                        live = optProps.get();
                        props = optProps.get();
                    }
                }
            }

            if (req.getGameRules() != null) {
                for (Map.Entry<String, String> entry : req.getGameRules().entrySet()) {
                    props.setGameRule(entry.getKey(), entry.getValue());
                }
            }

            req.getGeneratorType().ifPresent(props::setGeneratorType);
            req.getGameMode().ifPresent(props::setGameMode);
            req.getDifficulty().ifPresent(props::setDifficulty);

            if (req.getSeed() != null) {
                props.setSeed(req.getSeed());
            }
            if (req.doesLoadOnStartup() != null) {
                props.setLoadOnStartup(req.doesLoadOnStartup());
            }
            if (req.doesKeepSpawnLoaded() != null) {
                props.setKeepSpawnLoaded(req.doesKeepSpawnLoaded());
            }
            if (req.doesAllowCommands() != null) {
                props.setCommandsAllowed(req.doesAllowCommands());
            }
            if (req.doesUseMapFeatures() != null) {
                props.setMapFeaturesEnabled(req.doesUseMapFeatures());
            }

            if (live instanceof World)
                return DataCache.updateWorld((World) live);
            else
                return DataCache.updateWorld((WorldProperties) live);
        });

        data.addJson("ok", resWorld.isPresent(), false);
        data.addJson("world", resWorld.orElse(null), true);
    }

    @Override
    @Permission(perm = "world.post")
    protected void handlePost(ServletData data) {
        final String[] paths = data.getPathParts();

        // A post directly to /api/world creates a new world
        if (paths.length < 1 || paths[0].isEmpty()) {
            WorldArchetype.Builder builder = WorldArchetype.builder();

            Optional<CreateWorldRequest> optReq = data.getRequestBody(CreateWorldRequest.class);
            if (!optReq.isPresent()) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid world data: " + data.getLastParseError().getMessage());
                return;
            }

            CreateWorldRequest req = optReq.get();

            if (req.getName().isEmpty()) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "No name provided");
                return;
            }

            req.getDimensionType().ifPresent(builder::dimension);
            req.getGeneratorType().ifPresent(builder::generator);
            req.getGameMode().ifPresent(builder::gameMode);
            req.getDifficulty().ifPresent(builder::difficulty);

            if (req.getSeed() != null) {
                builder.seed(req.getSeed());
            }
            if (req.doesLoadOnStartup() != null) {
                builder.loadsOnStartup(req.doesLoadOnStartup());
            }
            if (req.doesKeepSpawnLoaded() != null) {
                builder.keepsSpawnLoaded(req.doesKeepSpawnLoaded());
            }
            if (req.doesAllowCommands() != null) {
                builder.commandsAllowed(req.doesAllowCommands());
            }
            if (req.doesGenerateBonusChest() != null) {
                builder.generateBonusChest(req.doesGenerateBonusChest());
            }
            if (req.doesUseMapFeatures() != null) {
                builder.usesMapFeatures(req.doesUseMapFeatures());
            }

            String archTypeName = UUID.randomUUID().toString();
            WorldArchetype archType = builder.enabled(true).build(archTypeName, archTypeName);

            Optional<WorldProperties> resProps = WebAPI.runOnMain(() -> {
                try {
                    return Sponge.getServer().createWorldProperties(req.getName(), archType);
                } catch (IOException e) {
                    data.addJson("ok", false, false);
                    data.addJson("error", e, false);
                    return null;
                }
            });

            if (!resProps.isPresent())
                return;

            CachedWorld world = DataCache.updateWorld(resProps.get());

            data.setStatus(HttpServletResponse.SC_CREATED);
            data.addJson("ok", true, false);
            data.addJson("world", world, true);
            data.setHeader("Location", world.getLink());
            return;
        }

        // A post to /api/world/{uuid}/{method} is handled here
        String uuid = paths[0];
        if (uuid.split("-").length != 5) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid world UUID");
            return;
        }

        Optional<CachedWorld> world = DataCache.getWorld(UUID.fromString(uuid));
        if (!world.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "World with UUID '" + uuid + "' could not be found");
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

                Optional<Object> res = DataCache.executeMethod(world.get(), mName, params.get().getFirst(), params.get().getSecond());
                if (!res.isPresent()) {
                    data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not get world");
                    return;
                }

                data.addJson("ok", true, false);
                data.addJson("world", world.get(), true);
                data.addJson("result", res.get(), true);
                break;

            default:
                data.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Invalid method");
        }
    }

    @Override
    @Permission(perm = "world.delete")
    protected void handleDelete(ServletData data) {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid world UUID");
            return;
        }

        String uuid = paths[0];
        if (uuid.split("-").length != 5) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid world UUID");
            return;
        }

        Optional<CachedWorld> optWorld = DataCache.getWorld(UUID.fromString(uuid));
        if (!optWorld.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "World with UUID '" + uuid + "' could not be found");
            return;
        }

        CachedWorld world = optWorld.get();

        Optional<Boolean> deleted = WebAPI.runOnMain(() -> {
            Optional<?> live = world.getLive();
            if (!live.isPresent())
                return false;

            WorldProperties w = (WorldProperties)live.get();
            try {
                return Sponge.getServer().deleteWorld(w).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return false;
        });

        if (!deleted.isPresent() || !deleted.get()) {
            data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not delete world " + uuid);
            return;
        }

        DataCache.removeWorld(world.getUUID());

        data.addJson("ok", true, false);
        data.addJson("world", world, true);
    }
}
