package valandur.webapi.servlet;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.storage.WorldProperties;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.misc.CachedCatalogType;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.api.cache.world.ICachedWorldFull;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.ExplicitDetails;
import valandur.webapi.api.servlet.Permission;
import valandur.webapi.cache.world.CachedWorld;
import valandur.webapi.serialize.objects.ExecuteMethodRequest;
import valandur.webapi.serialize.objects.ExecuteMethodResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Path("world")
@Api(tags = { "World" }, value = "List all worlds and get detailed information about them.")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class WorldServlet extends BaseServlet {

    @GET
    @ExplicitDetails
    @Permission("list")
    @ApiOperation(
            value = "List worlds",
            notes = "Get a list of all the worlds on the server.")
    public Collection<ICachedWorldFull> listWorlds() {
        return cacheService.getWorlds();
    }

    @GET
    @Path("/{world}")
    @Permission("one")
    @ApiOperation(
            value = "Get a world",
            notes = "Get detailed information about a world.")
    public ICachedWorldFull getWorld(
            @PathParam("world") @ApiParam("The uuid of the world for which to get details") ICachedWorldFull world) {
        return world;
    }

    @POST
    @Permission("create")
    @ApiOperation(
            value = "Create a world",
            response = ICachedWorldFull.class,
            notes = "Creates a new world with the specified settings. This does not yet load the world.")
    public Response createWorld(CreateWorldRequest req)
            throws BadRequestException, URISyntaxException {
        if (req == null) {
            throw new BadRequestException("Request body is required");
        }

        WorldArchetype.Builder builder = WorldArchetype.builder();
        if (req.getName() == null || req.getName().isEmpty()) {
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
        WorldArchetype archType = builder.generateSpawnOnLoad(true).enabled(true).build(archTypeName, archTypeName);

        ICachedWorld world = WebAPI.runOnMain(() -> {
            try {
                WorldProperties props = Sponge.getServer().createWorldProperties(req.getName(), archType);
                return cacheService.updateWorld(props);
            } catch (IOException e) {
                throw new InternalServerErrorException(e.getMessage());
            }
        });

        return Response.created(new URI(null, null, world.getLink(), null)).entity(world).build();
    }

    @PUT
    @Path("/{world}")
    @Permission("modify")
    @ApiOperation(
            value = "Modify a world",
            notes = "Modify the properties of an existing world.")
    public ICachedWorldFull modifyWorld(
            @PathParam("world") @ApiParam("The uuid of the world which to update") ICachedWorldFull world,
            UpdateWorldRequest req) {

        if (req == null) {
            throw new BadRequestException("Request body is required");
        }

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
    @ApiOperation(
            value = "Delete a world",
            notes = "Deletes an existing world. **The world must be unloaded before deleting it**")
    public ICachedWorldFull deleteWorld(
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
    @ApiOperation(
            value = "Execute a method",
            notes = "Provides direct access to the underlaying world object and can execute any method on it.")
    public ExecuteMethodResponse executeMethod(
            @PathParam("world") @ApiParam("The uuid of the world on which to execute the method") ICachedWorldFull world,
            ExecuteMethodRequest req)
            throws BadRequestException {

        if (req == null) {
            throw new BadRequestException("Request body is required");
        }

        if (req.getMethod() == null || req.getMethod().isEmpty()) {
            throw new BadRequestException("Method must be specified");
        }

        String mName = req.getMethod();
        Tuple<Class[], Object[]> params = req.getParsedParameters();
        Object res = cacheService.executeMethod(world, mName, params.getFirst(), params.getSecond());
        return new ExecuteMethodResponse(world, res);
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
        @ApiModelProperty(value = "Which generator to use for the world")
        public Optional<GeneratorType> getGenerator() {
            return generator != null ? generator.getLive(GeneratorType.class) : Optional.empty();
        }

        private CachedCatalogType<GameMode> gameMode;
        @ApiModelProperty(value = "Which game mode the world defaults to")
        public Optional<GameMode> getGameMode() {
            return gameMode != null ? gameMode.getLive(GameMode.class) : Optional.empty();
        }

        private CachedCatalogType<Difficulty> difficulty;
        @ApiModelProperty(value = "Which difficulty the world is set to")
        public Optional<Difficulty> getDifficulty() {
            return difficulty != null ? difficulty.getLive(Difficulty.class) : Optional.empty();
        }

        @JsonProperty("loadOnStartup")
        private Boolean loadOnStartup;
        @ApiModelProperty(value = "True if the world should be loaded when the server starts, false otherwise.")
        public Boolean loadOnStartup() {
            return loadOnStartup;
        }

        @JsonProperty("keepSpawnLoaded")
        private Boolean keepSpawnLoaded;
        @ApiModelProperty("True if the world spawn should be kept loaded, even if no players are present, false otherwise")
        public Boolean keepSpawnLoaded() {
            return keepSpawnLoaded;
        }

        @JsonProperty("allowCommands")
        private Boolean allowCommands;
        @ApiModelProperty("True if this world allows running commands, false otherwise")
        public Boolean allowCommands() {
            return allowCommands;
        }

        @JsonProperty("usesMapFeatures")
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
        public Boolean generateBonusChest() {
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
