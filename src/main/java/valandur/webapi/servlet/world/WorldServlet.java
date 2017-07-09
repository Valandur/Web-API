package valandur.webapi.servlet.world;

import com.fasterxml.jackson.databind.JsonNode;
import com.flowpowered.math.vector.Vector3i;
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
import valandur.webapi.cache.world.CachedWorld;
import valandur.webapi.servlet.ServletData;
import valandur.webapi.util.Util;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@WebAPIServlet(basePath = "world")
public class WorldServlet extends WebAPIBaseServlet {

    private static int TILE_SIZE = 512;
    private static int HALF_TILE_SIZE = TILE_SIZE / 2;
    private Map<String, String> biomeColorMap = new HashMap<>();


    public WorldServlet() {
        biomeColorMap.put("minecraft:ocean", "000070");
        biomeColorMap.put("minecraft:plains", "8DB360");
        biomeColorMap.put("minecraft:desert", "FA9418");
        biomeColorMap.put("minecraft:extreme_hills", "606060");
        biomeColorMap.put("minecraft:forest", "056621");
        biomeColorMap.put("minecraft:taiga", "0B6659");
        biomeColorMap.put("minecraft:swampland", "07F9B2");
        biomeColorMap.put("minecraft:river", "0000FF");
        biomeColorMap.put("minecraft:hell", "FF0000");
        biomeColorMap.put("minecraft:sky", "8080FF");
        biomeColorMap.put("minecraft:frozen_ocean", "9090A0");
        biomeColorMap.put("minecraft:frozen_river", "A0A0FF");
        biomeColorMap.put("minecraft:ice_flats", "FFFFFF");
        biomeColorMap.put("minecraft:ice_mountains", "A0A0A0");
        biomeColorMap.put("minecraft:mushroom_island", "FF00FF");
        biomeColorMap.put("minecraft:mushroom_island_shore", "A000FF");
        biomeColorMap.put("minecraft:beaches", "FADE55");
        biomeColorMap.put("minecraft:desert_hills", "D25F12");
        biomeColorMap.put("minecraft:forest_hills", "22551C");
        biomeColorMap.put("minecraft:taiga_hills", "163933");
        biomeColorMap.put("minecraft:smaller_extreme_hills", "72789A");
        biomeColorMap.put("minecraft:jungle", "537B09");
        biomeColorMap.put("minecraft:jungle_hills", "2C4205");
        biomeColorMap.put("minecraft:jungle_edge", "628B17");
        biomeColorMap.put("minecraft:deep_ocean", "000030");
        biomeColorMap.put("minecraft:stone_beach", "A2A284");
        biomeColorMap.put("minecraft:cold_beach", "FAF0C0");
        biomeColorMap.put("minecraft:birch_forest", "307444");
        biomeColorMap.put("minecraft:birch_forest_hills", "1F5F32");
        biomeColorMap.put("minecraft:roofed_forest", "40511A");
        biomeColorMap.put("minecraft:taiga_cold", "31554A");
        biomeColorMap.put("minecraft:taiga_cold_hills", "243F36");
        biomeColorMap.put("minecraft:redwood_taiga", "596651");
        biomeColorMap.put("minecraft:redwood_taiga_hills", "545F3E");
        biomeColorMap.put("minecraft:extreme_hills_with_trees", "507050");
        biomeColorMap.put("minecraft:savanna", "BDB25F");
        biomeColorMap.put("minecraft:savanna_rock", "A79D64");
        biomeColorMap.put("minecraft:mesa", "D94515");
        biomeColorMap.put("minecraft:mesa_rock", "B09765");
        biomeColorMap.put("minecraft:mesa_clear_rock", "CA8C65");
    }

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

    @WebAPIEndpoint(method = HttpMethod.GET, path = "/:world/chunk/:x/:z", perm = "chunk")
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
        data.addJson("chunk", chunk.get(), true);
    }

    @WebAPIEndpoint(method = HttpMethod.POST, path = "/:world/chunk/:x/:z", perm = "chunk")
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
        data.addJson("chunk", chunk.get(), true);
    }

    @WebAPIEndpoint(method = HttpMethod.GET, path = "/:world/map/:x/:z", perm = "map")
    public void getMap(ServletData data, CachedWorld world, int x, int z) {
        int bX = TILE_SIZE * x;
        int bZ = TILE_SIZE * z;
        Vector3i min = new Vector3i(bX - HALF_TILE_SIZE, 0, bZ - HALF_TILE_SIZE);
        Vector3i max = new Vector3i(bX + HALF_TILE_SIZE, 0, bZ + HALF_TILE_SIZE);

        String fileName = "tile-x" + x + "z" + z + ".png";
        Path filePath = Paths.get("webapi/cache/" + world.getUUID() + "/" + fileName);
        if (!filePath.getParent().toFile().exists())
            filePath.getParent().toFile().mkdirs();

        if (Files.exists(filePath)) {
            try {
                Files.copy(filePath, data.getOutputStream());
                data.setContentType("image/png");
                data.setDone();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Optional<String[][]> optBiomes = blockService.getBiomes(world, min, max);
        if (!optBiomes.isPresent()) {
            data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not get biomes");
            return;
        }

        String[][] biomes = optBiomes.get();
        BufferedImage img = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();

        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[i].length; j++) {
                String biome = biomes[i][j].replace("mutated_", "");

                String hexColor = biomeColorMap.get(biome);
                if (hexColor == null) {
                    WebAPI.getLogger().info("Unkown biome: " + biome);

                    hexColor = "FFFFFF";
                    biomeColorMap.put(biome, hexColor);
                }

                g2.setColor(Color.decode("#" + hexColor));
                g2.fillRect(i * 4, img.getHeight() - j * 4, 4, 4);
            }
        }

        try {
            ImageIO.write(img, "PNG", new File(filePath.toString()));
            ImageIO.write(img, "PNG", data.getOutputStream());

            data.setContentType("image/png");
            data.setDone();
        } catch (IOException ignored) {}
    }
}
