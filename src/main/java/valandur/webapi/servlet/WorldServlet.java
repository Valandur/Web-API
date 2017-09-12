package valandur.webapi.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.jetty.http.HttpMethod;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.storage.WorldProperties;
import valandur.webapi.WebAPI;
import valandur.webapi.api.annotation.WebAPIEndpoint;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.api.servlet.WebAPIBaseServlet;
import valandur.webapi.cache.world.CachedChunk;
import valandur.webapi.cache.world.CachedWorld;
import valandur.webapi.json.request.world.CreateWorldRequest;
import valandur.webapi.json.request.world.UpdateWorldRequest;
import valandur.webapi.servlet.base.ServletData;
import valandur.webapi.util.Util;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

@WebAPIServlet(basePath = "world")
public class WorldServlet extends WebAPIBaseServlet {

    @WebAPIEndpoint(method = HttpMethod.GET, path = "/", perm = "list")
    public void getWorlds(ServletData data) {
        data.addJson("ok", true, false);
        data.addJson("worlds", cacheService.getWorlds(), data.getQueryParam("details").isPresent());
    }

    @WebAPIEndpoint(method = HttpMethod.GET, path = "/:world", perm = "one")
    public void getWorld(ServletData data, CachedWorld world) {
        Optional<String> strFields = data.getQueryParam("fields");
        Optional<String> strMethods = data.getQueryParam("methods");
        if (strFields.isPresent() || strMethods.isPresent()) {
            String[] fields = strFields.map(s -> s.split(",")).orElse(new String[]{});
            String[] methods = strMethods.map(s -> s.split(",")).orElse(new String[]{});
            Tuple extra = cacheService.getExtraData(world, fields, methods);
            data.addJson("fields", extra.getFirst(), true);
            data.addJson("methods", extra.getSecond(), true);
        }

        data.addJson("ok", true, false);
        data.addJson("world", world, true);
    }

    @WebAPIEndpoint(method = HttpMethod.POST, path = "/", perm = "create")
    public void createWorld(ServletData data) {
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

        ICachedWorld world = cacheService.updateWorld(resProps.get());

        data.setStatus(HttpServletResponse.SC_CREATED);
        data.addJson("ok", true, false);
        data.addJson("world", world, true);
        data.setHeader("Location", world.getLink());
    }

    @WebAPIEndpoint(method = HttpMethod.POST, path = "/:world/method", perm = "method")
    public void executeMethod(ServletData data, CachedWorld world) {
        JsonNode reqJson = data.getRequestBody();
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

        Optional<Object> res = cacheService.executeMethod(world, mName, params.get().getFirst(), params.get().getSecond());
        if (!res.isPresent()) {
            data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not get world");
            return;
        }

        data.addJson("ok", true, false);
        data.addJson("world", world, true);
        data.addJson("result", res.get(), true);
    }

    @WebAPIEndpoint(method = HttpMethod.PUT, path = "/:world", perm = "change")
    public void updateWorld(ServletData data, CachedWorld world) {
        Optional<UpdateWorldRequest> optReq = data.getRequestBody(UpdateWorldRequest.class);
        if (!optReq.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid world data: " + data.getLastParseError().getMessage());
            return;
        }

        final UpdateWorldRequest req = optReq.get();

        Optional<ICachedWorld> resWorld = WebAPI.runOnMain(() -> {
            Optional<?> optLive = world.getLive();
            if (!optLive.isPresent())
                return null;

            Object live = optLive.get();
            WorldProperties props = live instanceof World ? ((World) live).getProperties() : (WorldProperties) live;

            if (req.isLoaded() != null && req.isLoaded() != world.isLoaded()) {
                if (req.isLoaded()) {
                    Optional<World> newWorld = Sponge.getServer().loadWorld(props);
                    if (newWorld.isPresent()) {
                        live = newWorld.get();
                        props = newWorld.get().getProperties();
                    }
                } else {
                    Sponge.getServer().unloadWorld((World)live);
                    Optional<WorldProperties> optProps = Sponge.getServer().getUnloadedWorlds()
                            .stream().filter(w -> w.getUniqueId().equals(world.getUUID())).findAny();
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
                return cacheService.updateWorld((World) live);
            else
                return cacheService.updateWorld((WorldProperties) live);
        });

        data.addJson("ok", resWorld.isPresent(), false);
        data.addJson("world", resWorld.orElse(null), true);
    }

    @WebAPIEndpoint(method = HttpMethod.DELETE, path = "/:world", perm = "delete")
    public void deleteWorld(ServletData data, CachedWorld world) {
        Optional<Boolean> deleted = WebAPI.runOnMain(() -> {
            Optional<?> live = world.getLive();
            if (!live.isPresent())
                return false;

            WorldProperties w = (WorldProperties)live.get();
            try {
                return Sponge.getServer().deleteWorld(w).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                if (WebAPI.reportErrors()) WebAPI.sentryCapture(e);
            }
            return false;
        });

        if (!deleted.isPresent() || !deleted.get()) {
            data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not delete world " + world.getName());
            return;
        }

        cacheService.removeWorld(world.getUUID());

        data.addJson("ok", true, false);
        data.addJson("world", world, true);
    }

    @WebAPIEndpoint(method = HttpMethod.GET, path = "/:world/chunk", perm = "chunk.list")
    public void getChunks(ServletData data, CachedWorld world) {
        Optional<?> optWorld = world.getLive();
        if (!optWorld.isPresent()) {
            data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not get world");
            return;
        }

        World w = (World)optWorld.get();
        List<CachedChunk> chunks = new ArrayList<>();

        Iterable<Chunk> iterable = w.getLoadedChunks();
        iterable.forEach(c -> chunks.add(new CachedChunk(c)));

        data.addJson("ok", true, false);
        data.addJson("chunks", chunks, data.getQueryParam("details").isPresent());
    }

    @WebAPIEndpoint(method = HttpMethod.GET, path = "/:world/chunk/:x/:z", perm = "chunk.one")
    public void getChunkAt(ServletData data, CachedWorld world, int x, int z) {
        Optional<Chunk> chunk = WebAPI.runOnMain(() -> {
            Optional<?> optLive = world.getLive();
            if (!optLive.isPresent())
                return null;

            World live = (World)optLive.get();
            return live.loadChunk(x, 0, z, false).orElse(null);
        });

        if (!chunk.isPresent()) {
            data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not get chunk");
            return;
        }

        data.addJson("ok", true, false);
        data.addJson("chunk", chunk.map(CachedChunk::new), true);
    }

    @WebAPIEndpoint(method = HttpMethod.POST, path = "/:world/chunk/:x/:z", perm = "chunk.create")
    public void createChunkAt(ServletData data, CachedWorld world, int x, int z) {
        Optional<Chunk> chunk = WebAPI.runOnMain(() -> {
            Optional<?> optLive = world.getLive();
            if (!optLive.isPresent())
                return null;

            World live = (World)optLive.get();
            return live.loadChunk(x, 0, z, true).orElse(null);
        });

        if (!chunk.isPresent()) {
            data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not generate chunk");
            return;
        }

        data.addJson("ok", true, false);
        data.addJson("chunk", chunk.map(CachedChunk::new), true);
    }
}
