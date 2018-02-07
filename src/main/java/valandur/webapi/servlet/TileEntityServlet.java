package valandur.webapi.servlet;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.api.cache.tileentity.ICachedTileEntity;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.ExplicitDetails;
import valandur.webapi.api.servlet.Permission;
import valandur.webapi.serialize.deserialize.ExecuteMethodRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

@Path("tile-entity")
@Api(tags = { "Tile Entity" }, value = "List all tile entities and get detailed information about them.")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class TileEntityServlet extends BaseServlet {

    @GET
    @ExplicitDetails
    @Permission("list")
    @ApiOperation(value = "List tile entities", notes =
            "Get a list of all tile entities on the server (in all worlds, unless specified).")
    public Collection<ICachedTileEntity> getTileEntities(
            @QueryParam("world") @ApiParam("The world to filter tile entities by") ICachedWorld world,
            @QueryParam("type") @ApiParam("The type if of tile entities to filter by") String typeId,
            @QueryParam("limit") @ApiParam("The maximum amount of tile entities returned") int limit) {

        Predicate<TileEntity> filter = te -> typeId == null || te.getType().getId().equalsIgnoreCase(typeId);

        if (world != null) {
            return cacheService.getTileEntities(world, filter, limit);
        }

        return cacheService.getTileEntities(filter, limit);
    }

    @GET
    @Path("/{world}/{x}/{y}/{z}")
    @Permission("one")
    @ApiOperation(value = "Get tile entity", notes = "Get detailed information about a tile entity.")
    public ICachedTileEntity getTileEntity(
            @PathParam("world") @ApiParam("The world the tile entity is in") ICachedWorld world,
            @PathParam("x") @ApiParam("The x-coordinate of the tile-entity") Integer x,
            @PathParam("y") @ApiParam("The y-coordinate of the tile-entity") Integer y,
            @PathParam("z") @ApiParam("The z-coordinate of the tile-entity") Integer z)
            throws NotFoundException {
        Optional<ICachedTileEntity> optTe = cacheService.getTileEntity(world, x, y, z);

        if (!optTe.isPresent()) {
            throw new NotFoundException("Tile entity in world '" + world.getName() +
                    "' at [" + x + "," + y + "," + z + "] could not be found");
        }

        return optTe.get();
    }

    @POST
    @Path("/{world}/{x}/{y}/{z}/method")
    @Permission("method")
    @ApiOperation(value = "Execute a method", notes =
            "Provides direct access to the underlaying tile entity object and can execute any method on it.")
    public Object executeMethod(
            @PathParam("world") @ApiParam("The world the tile entity is in") ICachedWorld world,
            @PathParam("x") @ApiParam("The x-coordinate of the tile-entity") Integer x,
            @PathParam("y") @ApiParam("The x-coordinate of the tile-entity") Integer y,
            @PathParam("z") @ApiParam("The x-coordinate of the tile-entity") Integer z,
            ExecuteMethodRequest req)
            throws BadRequestException, NotFoundException {

        if (req == null) {
            throw new BadRequestException("Request body is required");
        }

        if (req.getMethod() == null || req.getMethod().isEmpty()) {
            throw new BadRequestException("Method must be specified");
        }

        Optional<ICachedTileEntity> te = cacheService.getTileEntity(world, x, y, z);
        if (!te.isPresent()) {
            throw new NotFoundException("Tile entity in world '" + world.getName() +
                    "' at [" + x + "," + y + "," + z + "] could not be found");
        }

        String mName = req.getMethod();
        Tuple<Class[], Object[]> params = req.getParsedParameters();
        return cacheService.executeMethod(te.get(), mName, params.getFirst(), params.getSecond());
    }
}
