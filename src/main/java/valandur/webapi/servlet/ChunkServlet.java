package valandur.webapi.servlet;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.ExplicitDetails;
import valandur.webapi.api.servlet.Permission;
import valandur.webapi.cache.world.CachedChunk;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Path("chunk")
@Api(tags = { "Chunk" }, value = "Get and force load certain chunks of a world")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class ChunkServlet extends BaseServlet {

    @GET
    @ExplicitDetails
    @Path("/{world}")
    @Permission({ "chunk", "list" })
    @ApiOperation(
            value = "List chunks",
            notes = "Gets a list of all the loaded chunks for the specified world.")
    public List<CachedChunk> listChunks(
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
    @Path("/{world}/{x}/{z}")
    @Permission({ "chunk", "one "})
    @ApiOperation(
            value = "Get a chunk",
            notes = "Get detailed information about a chunk")
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
    @Path("/{world}/{x}/{z}")
    @Permission({ "chunk", "create" })
    @ApiOperation(
            value = "Load & Generate a chunk",
            response = CachedChunk.class,
            notes = "Forces a chunk to be loaded into memory, and created if it does not exist.")
    public Response createChunkAt(
            @PathParam("world") @ApiParam("The uuid of the world in which to create the chunk") ICachedWorld world,
            @PathParam("x") @ApiParam("The x-coordinate of the chunk (in chunk coordinates)") int x,
            @PathParam("z") @ApiParam("The z-coordinate of the chunk (in chunk coordinates)") int z)
            throws URISyntaxException {
        CachedChunk chunk = WebAPI.runOnMain(() -> {
            Optional<World> optLive = world.getLive();
            if (!optLive.isPresent())
                throw new InternalServerErrorException("Could not get live world");

            World live = optLive.get();
            Optional<Chunk> optChunk = live.loadChunk(x, 0, z, true);
            if (!optChunk.isPresent())
                throw new InternalServerErrorException("Could not load live chunk");
            return new CachedChunk(optChunk.get());
        });

        return Response.created(new URI(null, null, chunk.getLink(), null)).entity(chunk).build();
    }
}
