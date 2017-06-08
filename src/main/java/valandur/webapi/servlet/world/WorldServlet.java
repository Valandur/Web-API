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
            data.addJson("worlds", DataCache.getWorlds(), false);
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

        String strFields = data.getQueryPart("fields");
        String strMethods = data.getQueryPart("methods");
        if (strFields != null || strMethods != null) {
            String[] fields = strFields != null ? strFields.split(",") : new String[]{};
            String[] methods = strMethods != null ? strMethods.split(",") : new String[]{};
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

        final JsonNode reqJson = data.getRequestBody();

        Optional<CachedWorld> resWorld = WebAPI.runOnMain(() -> {
            Optional<?> optLive = world.get().getLive();
            if (!optLive.isPresent())
                return null;

            Object live = optLive.get();
            WorldProperties props = live instanceof World ? ((World) live).getProperties() : (WorldProperties) live;

            if (reqJson.has("loaded")) {
                boolean loaded = reqJson.get("loaded").asBoolean();
                if (loaded != world.get().isLoaded()) {
                    if (loaded) {
                        Optional<World> newWorld = Sponge.getServer().loadWorld(props);
                        if (newWorld.isPresent()) {
                            live = newWorld.get();
                            props = newWorld.get().getProperties();
                        }
                    } else {
                        Sponge.getServer().unloadWorld((World)live);
                    }
                }
            }

            if (reqJson.has("rules")) {
                Map<String, String> gameRules = props.getGameRules();
                Iterator<Map.Entry<String, JsonNode>> nodes = reqJson.get("rules").fields();
                while (nodes.hasNext()) {
                    Map.Entry<String, JsonNode> entry = nodes.next();
                    props.setGameRule(entry.getKey(), entry.getValue().asText());
                }
            }

            if (reqJson.has("generator")) {
                String gen = reqJson.get("generator").asText();
                Collection<GeneratorType> types = Sponge.getRegistry().getAllOf(GeneratorType.class);
                Optional<GeneratorType> type = types.stream().filter(g -> g.getId().equalsIgnoreCase(gen) || g.getName().equalsIgnoreCase(gen)).findAny();
                type.ifPresent(props::setGeneratorType);
            }

            if (reqJson.has("seed")) {
                if (reqJson.get("seed").isLong()) {
                    props.setSeed(reqJson.get("seed").asLong());
                } else {
                    props.setSeed(reqJson.get("seed").asInt());
                }
            }

            if (reqJson.has("gameMode")) {
                String gm = reqJson.get("gameMode").asText();
                Collection<GameMode> types = Sponge.getRegistry().getAllOf(GameMode.class);
                Optional<GameMode> type = types.stream().filter(g -> g.getId().equalsIgnoreCase(gm) || g.getName().equalsIgnoreCase(gm)).findAny();
                type.ifPresent(props::setGameMode);
            }

            if (reqJson.has("difficulty")) {
                String diff = reqJson.get("difficulty").asText();
                Collection<Difficulty> types = Sponge.getRegistry().getAllOf(Difficulty.class);
                Optional<Difficulty> type = types.stream().filter(g -> g.getId().equalsIgnoreCase(diff) || g.getName().equalsIgnoreCase(diff)).findAny();
                type.ifPresent(props::setDifficulty);
            }

            if (reqJson.has("loadOnStartup")) {
                props.setLoadOnStartup(reqJson.get("loadOnStartup").asBoolean());
            }

            if (reqJson.has("keepSpawnLoaded")) {
                props.setKeepSpawnLoaded(reqJson.get("keepSpawnLoaded").asBoolean());
            }

            if (reqJson.has("commandsAllowed")) {
                props.setCommandsAllowed(reqJson.get("commandsAllowed").asBoolean());
            }

            if (reqJson.has("usesMapFeatures")) {
                props.setMapFeaturesEnabled(reqJson.get("usesMapFeatures").asBoolean());
            }

            if (live instanceof World)
                return DataCache.updateWorld((World) live);
            else
                return DataCache.updateWorld((WorldProperties) live);
        });

        data.addJson("ok", resWorld.isPresent(), false);
        data.addJson("world", resWorld.orElse(null), false);
    }

    @Override
    @Permission(perm = "world.post")
    protected void handlePost(ServletData data) {
        final String[] paths = data.getPathParts();
        final JsonNode reqJson = data.getRequestBody();

        // A post directly to /api/world creates a new world
        if (paths.length < 1 || paths[0].isEmpty()) {
            WorldArchetype.Builder builder = WorldArchetype.builder();

            Optional<CreateWorldRequest> optReq = data.getRequestBody(CreateWorldRequest.class);
            if (!optReq.isPresent()) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid world data");
                return;
            }

            CreateWorldRequest req = optReq.get();

            if (req.getName().isEmpty()) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "No name provided");
                return;
            }

            if (reqJson.has("dimension")) {
                String dim = reqJson.get("dimension").asText();
                Collection<DimensionType> types = Sponge.getRegistry().getAllOf(DimensionType.class);
                Optional<DimensionType> type = types.stream().filter(t -> t.getId().equalsIgnoreCase(dim) || t.getName().equalsIgnoreCase(dim)).findAny();
                type.ifPresent(builder::dimension);
            }

            if (reqJson.has("generator")) {
                String gen = reqJson.get("generator").asText();
                Collection<GeneratorType> types = Sponge.getRegistry().getAllOf(GeneratorType.class);
                Optional<GeneratorType> type = types.stream().filter(g -> g.getId().equalsIgnoreCase(gen) || g.getName().equalsIgnoreCase(gen)).findAny();
                type.ifPresent(builder::generator);
            }

            if (reqJson.has("seed")) {
                if (reqJson.get("seed").isLong()) {
                    builder.seed(reqJson.get("seed").asLong());
                } else {
                    builder.seed(reqJson.get("seed").asInt());
                }
            }

            if (reqJson.has("gameMode")) {
                String gm = reqJson.get("gameMode").asText();
                Collection<GameMode> types = Sponge.getRegistry().getAllOf(GameMode.class);
                Optional<GameMode> type = types.stream().filter(g -> g.getId().equalsIgnoreCase(gm) || g.getName().equalsIgnoreCase(gm)).findAny();
                type.ifPresent(builder::gameMode);
            }

            if (reqJson.has("difficulty")) {
                String diff = reqJson.get("difficulty").asText();
                Collection<Difficulty> types = Sponge.getRegistry().getAllOf(Difficulty.class);
                Optional<Difficulty> type = types.stream().filter(g -> g.getId().equalsIgnoreCase(diff) || g.getName().equalsIgnoreCase(diff)).findAny();
                type.ifPresent(builder::difficulty);
            }

            if (reqJson.has("loadOnStartup")) {
                builder.loadsOnStartup(reqJson.get("loadOnStartup").asBoolean());
            }

            if (reqJson.has("keepSpawnLoaded")) {
                builder.keepsSpawnLoaded(reqJson.get("keepSpawnLoaded").asBoolean());
            }

            if (reqJson.has("commandsAllowed")) {
                builder.commandsAllowed(reqJson.get("commandsAllowed").asBoolean());
            }

            if (reqJson.has("generateBonusChest")) {
                builder.generateBonusChest(reqJson.get("generateBonusChest").asBoolean());
            }

            if (reqJson.has("usesMapFeatures")) {
                builder.usesMapFeatures(reqJson.get("usesMapFeatures").asBoolean());
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
            data.addJson("world", world, false);
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

        Optional<CachedWorld> world = DataCache.getWorld(UUID.fromString(uuid));
        if (!world.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "World with UUID '" + uuid + "' could not be found");
            return;
        }

        Optional<Boolean> deleted = WebAPI.runOnMain(() -> {
            Optional<?> live = world.get().getLive();
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

        DataCache.updateWorlds();

        data.addJson("ok", true, false);
    }
}
