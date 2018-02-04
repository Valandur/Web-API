package valandur.webapi.servlet;

import io.swagger.annotations.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.*;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.storage.WorldProperties;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.ExplicitDetails;
import valandur.webapi.api.servlet.Permission;
import valandur.webapi.cache.misc.CachedCatalogType;
import valandur.webapi.cache.world.CachedChunk;
import valandur.webapi.cache.world.CachedWorld;
import valandur.webapi.serialize.deserialize.ExecuteMethodRequest;
import valandur.webapi.util.Util;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Path("world")
@Api(value = "world", tags = { "World" })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class WorldServlet extends BaseServlet {

    @GET
    @ExplicitDetails
    @Permission("list")
    @ApiOperation(value = "List worlds", notes = "Get a list of all the worlds on the server.")
    public Collection<ICachedWorld> getWorlds() {
        return cacheService.getWorlds();
    }

    @GET
    @Path("/{world}")
    @Permission("one")
    @ApiOperation(value = "Get a world", notes = "Get detailed information about a world.")
    public ICachedWorld getWorld(
            @PathParam("world") @ApiParam("The uuid of the world for which to get details") ICachedWorld world) {
        return world;
    }

    @POST
    @Permission("create")
    @ApiOperation(value = "Create a world", notes =
            "Creates a new world with the specified settings. This does not yet load the world.")
    public ICachedWorld createWorld(CreateWorldRequest req)
            throws BadRequestException {
        WorldArchetype.Builder builder = WorldArchetype.builder();
        if (req.getName().isEmpty()) {
            throw new BadRequestException("World requires a name");
        }

        req.getDimension().ifPresent(builder::dimension);
        req.getGenerator().ifPresent(builder::generator);
        req.getGameMode().ifPresent(builder::gameMode);
        req.getDifficulty().ifPresent(builder::difficulty);

        if (req.getSeed() != null) {
            builder.seed(req.getSeed());
        }

        if (req.loadOnStartup() != null)
            builder.loadsOnStartup(req.loadOnStartup());
        if (req.keepSpawnLoaded() != null)
            builder.keepsSpawnLoaded(req.keepSpawnLoaded());
        if (req.allowCommands() != null)
            builder.commandsAllowed(req.allowCommands());
        if (req.generateBonusChest != null)
            builder.generateBonusChest(req.generateBonusChest());
        if (req.usesMapFeatures() != null)
            builder.usesMapFeatures(req.usesMapFeatures());

        String archTypeName = "WebAPI-" + UUID.randomUUID().toString();
        WorldArchetype archType = builder.enabled(true).build(archTypeName, archTypeName);

        return WebAPI.runOnMain(() -> {
            try {
                WorldProperties props = Sponge.getServer().createWorldProperties(req.getName(), archType);
                return cacheService.updateWorld(props);
            } catch (IOException e) {
                throw new InternalServerErrorException(e.getMessage());
            }
        });
    }

    @PUT
    @Path("/{world}")
    @Permission("change")
    @ApiOperation(value = "Change a world", notes = "Update the properties of an existing world.")
    public ICachedWorld updateWorld(
            @PathParam("world") @ApiParam("The uuid of the world which to update") ICachedWorld world,
            UpdateWorldRequest req) {

        return WebAPI.runOnMain(() -> {
            Optional<World> optLive = world.getLive();
            Optional<WorldProperties> optProps = world.getLiveProps();
            if (!optProps.isPresent())
                throw new InternalServerErrorException("Could not get live world properties");

            World live = optLive.orElse(null);
            WorldProperties props = optProps.get();

            if (req.isLoaded() != null && req.isLoaded() != world.isLoaded()) {
                if (req.isLoaded()) {
                    Optional<World> newWorld = Sponge.getServer().loadWorld(props);
                    if (newWorld.isPresent()) {
                        live = newWorld.get();
                        props = newWorld.get().getProperties();
                    }
                } else if (live != null) {
                    Sponge.getServer().unloadWorld(live);
                    Optional<WorldProperties> newProps = Sponge.getServer().getUnloadedWorlds()
                            .stream().filter(w -> w.getUniqueId().equals(world.getUUID())).findAny();
                    if (newProps.isPresent()) {
                        live = null;
                        props = optProps.get();
                    }
                } else {
                    WebAPI.getLogger().warn("World should be unloaded but isn't present");
                    throw new InternalServerErrorException("World should be unloaded but isn't present");
                }
            }

            if (req.getGameRules() != null) {
                for (Map.Entry<String, String> entry : req.getGameRules().entrySet()) {
                    props.setGameRule(entry.getKey(), entry.getValue());
                }
            }

            req.getGenerator().ifPresent(props::setGeneratorType);
            req.getGameMode().ifPresent(props::setGameMode);
            req.getDifficulty().ifPresent(props::setDifficulty);

            if (req.getSeed() != null) {
                props.setSeed(req.getSeed());
            }

            if (req.loadOnStartup() != null)
                props.setLoadOnStartup(req.loadOnStartup());
            if (req.keepSpawnLoaded() != null)
                props.setKeepSpawnLoaded(req.keepSpawnLoaded());
            if (req.allowCommands() != null)
                props.setCommandsAllowed(req.allowCommands());
            if (req.usesMapFeatures() != null)
                props.setMapFeaturesEnabled(req.usesMapFeatures());

            if (live != null)
                return cacheService.updateWorld(live);
            else
                return cacheService.updateWorld(props);
        });
    }

    @DELETE
    @Path("/{world}")
    @Permission("delete")
    @ApiOperation(value = "Delete a world", notes =
            "Deletes an existing world. **The world must be unloaded before deleting it**")
    public CachedWorld deleteWorld(
            @PathParam("world") @ApiParam("The uuid of the world to delete") CachedWorld world) {
        boolean deleted = WebAPI.runOnMain(() -> {
            Optional<WorldProperties> optLive = world.getLiveProps();
            if (!optLive.isPresent())
                throw new InternalServerErrorException("Could not get live world properties");

            WorldProperties live = optLive.get();
            try {
                return Sponge.getServer().deleteWorld(live).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new InternalServerErrorException(e.getMessage());
            }
        });

        cacheService.removeWorld(world.getUUID());
        return world;
    }

    @POST
    @Path("/{world}/method")
    @Permission("method")
    @ApiOperation(value = "Execute a method", notes =
            "Provides direct access to the underlaying world object and can execute any method on it.")
    public Object executeMethod(
            @PathParam("world") @ApiParam("The uuid of the world on which to execute the method") ICachedWorld world,
            ExecuteMethodRequest req)
            throws BadRequestException {
        if (req.getMethod() == null || req.getMethod().isEmpty()) {
            throw new BadRequestException("Method must be specified");
        }

        String mName = req.getMethod();
        Optional<Tuple<Class[], Object[]>> params = Util.parseParams(req.getParameters());

        if (!params.isPresent()) {
            throw new BadRequestException("Invalid parameters");
        }

        return cacheService.executeMethod(world, mName, params.get().getFirst(), params.get().getSecond());
    }

    @GET
    @ExplicitDetails
    @Path("/{world}/chunk")
    @Permission({ "chunk", "list" })
    @ApiOperation(value = "List chunks", tags = { "Chunk" }, notes =
            "Gets a list of all the loaded chunks for the specified world.")
    public List<CachedChunk> getChunks(
            @PathParam("world") @ApiParam("The uuid of the for which to get all chunks") ICachedWorld world) {
        return WebAPI.runOnMain(() -> {
            Optional<World> optWorld = world.getLive();
            if (!optWorld.isPresent())
                throw new InternalServerErrorException("Could not get live world");

            World live = optWorld.get();
            List<CachedChunk> chunks = new ArrayList<>();

            Iterable<Chunk> iterable = live.getLoadedChunks();
            iterable.forEach(c -> chunks.add(new CachedChunk(c)));

            return chunks;
        });
    }

    @GET
    @Path("/{world}/chunk/{x}/{z}")
    @Permission({ "chunk", "one "})
    @ApiOperation(value = "Get a chunk", tags = { "Chunk" }, notes = "Get detailed information about a chunk")
    public CachedChunk getChunkAt(
            @PathParam("world") @ApiParam("The uuid of the world in which to get the chunk") ICachedWorld world,
            @PathParam("x") @ApiParam("The x-coordinate of the chunk (in chunk coordinates)") int x,
            @PathParam("z") @ApiParam("The z-coordinate of the chunk (in chunk coordinates)") int z) {
        return WebAPI.runOnMain(() -> {
            Optional<World> optLive = world.getLive();
            if (!optLive.isPresent())
                throw new InternalServerErrorException("Could not get live world");

            World live = optLive.get();
            Optional<Chunk> chunk = live.loadChunk(x, 0, z, false);
            return chunk.map(CachedChunk::new).orElse(null);
        });
    }

    @POST
    @Path("/{world}/chunk/{x}/{z}")
    @Permission({ "chunk", "create" })
    @ApiOperation(value = "Load & Generate a chunk", tags = { "Chunk" }, notes =
            "Forces a chunk to be loaded into memory, and created if it does not exist.")
    public CachedChunk createChunkAt(
            @PathParam("world") @ApiParam("The uuid of the world in which to create the chunk") ICachedWorld world,
            @PathParam("x") @ApiParam("The x-coordinate of the chunk (in chunk coordinates)") int x,
            @PathParam("z") @ApiParam("The z-coordinate of the chunk (in chunk coordinates)") int z) {
        return WebAPI.runOnMain(() -> {
            Optional<World> optLive = world.getLive();
            if (!optLive.isPresent())
                throw new InternalServerErrorException("Could not get live world");

            World live = optLive.get();
            Optional<Chunk> chunk = live.loadChunk(x, 0, z, true);
            return chunk.map(CachedChunk::new).orElse(null);
        });
    }


    @ApiModel("BaseWorldRequest")
    public static class BaseWorldRequest {

        private String name;
        @ApiModelProperty(value = "The name of the world", required = true)
        public String getName() {
            return name;
        }

        private Long seed;
        @ApiModelProperty("The seed of the world")
        public Long getSeed() {
            return seed;
        }

        private CachedCatalogType<GeneratorType> generator;
        @ApiModelProperty(dataType = "string", value = "Which generator to use for the world")
        public Optional<GeneratorType> getGenerator() {
            return generator != null ? generator.getLive(GeneratorType.class) : Optional.empty();
        }

        private CachedCatalogType<GameMode> gameMode;
        @ApiModelProperty(dataType = "string", value = "Which game mode the world defaults to")
        public Optional<GameMode> getGameMode() {
            return gameMode != null ? gameMode.getLive(GameMode.class) : Optional.empty();
        }

        private CachedCatalogType<Difficulty> difficulty;
        @ApiModelProperty(dataType = "string", value = "Which difficulty the world is set to")
        public Optional<Difficulty> getDifficulty() {
            return difficulty != null ? difficulty.getLive(Difficulty.class) : Optional.empty();
        }

        private Boolean loadOnStartup;
        @ApiModelProperty(value = "True if the world should be loaded when the server starts, false otherwise.")
        public Boolean loadOnStartup() {
            return loadOnStartup;
        }

        private Boolean keepSpawnLoaded;
        @ApiModelProperty("True if the world spawn should be kept loaded, even if no players are present, false otherwise")
        public Boolean keepSpawnLoaded() {
            return keepSpawnLoaded;
        }

        private Boolean allowCommands;
        @ApiModelProperty("True if this world allows running commands, false otherwise")
        public Boolean allowCommands() {
            return allowCommands;
        }

        private Boolean usesMapFeatures;
        @ApiModelProperty("True if this world uses special map features, false otherwise")
        public Boolean usesMapFeatures() {
            return usesMapFeatures;
        }
    }

    @ApiModel("CreateWorldRequest")
    public static class CreateWorldRequest extends BaseWorldRequest {

        private CachedCatalogType<DimensionType> dimension;
        @ApiModelProperty(dataType = "string", value = "The the dimension that this world belongs to")
        public Optional<DimensionType> getDimension() {
            return dimension != null ? dimension.getLive(DimensionType.class) : Optional.empty();
        }

        private Boolean generateBonusChest;
        @ApiModelProperty("True if this world should generate bonus chests, false otherwise")
        public boolean generateBonusChest() {
            return generateBonusChest;
        }
    }

    @ApiModel("UpdateWorldRequest")
    public static class UpdateWorldRequest extends BaseWorldRequest {

        private Boolean loaded;
        @ApiModelProperty("True if the world should be loaded, false otherwise")
        public Boolean isLoaded() {
            return loaded;
        }

        private Map<String, String> gameRules;
        @ApiModelProperty("The game rule settings of this world")
        public Map<String, String> getGameRules() {
            return gameRules;
        }
    }
}
