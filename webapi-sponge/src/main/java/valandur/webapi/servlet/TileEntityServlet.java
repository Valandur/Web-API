package valandur.webapi.servlet;

import com.flowpowered.math.vector.Vector3i;
import io.swagger.annotations.*;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.tileentity.CachedTileEntity;
import valandur.webapi.cache.world.CachedWorld;
import valandur.webapi.serialize.objects.ExecuteMethodRequest;
import valandur.webapi.serialize.objects.ExecuteMethodResponse;
import valandur.webapi.servlet.base.BaseServlet;
import valandur.webapi.servlet.base.ExplicitDetails;
import valandur.webapi.servlet.base.Permission;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.List;
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
    @ApiOperation(
            value = "List tile entities",
            notes = "Get a list of all tile entities on the server (in all worlds, unless specified).")
    public Collection<CachedTileEntity> listTileEntities(
            @QueryParam("world") @ApiParam("The world to filter tile entities by") CachedWorld world,
            @QueryParam("type") @ApiParam("The type if of tile entities to filter by") String typeId,
            @QueryParam("min") @ApiParam("The minimum coordinates at which the tile entity must be, min=x|y|z") Vector3i min,
            @QueryParam("max") @ApiParam("The maximum coordinates at which the tile entity must be, max=x|y|z") Vector3i max,
            @QueryParam("limit") @ApiParam("The maximum amount of tile entities returned") int limit) {

        Predicate<TileEntity> filter = te -> typeId == null || te.getType().getId().equalsIgnoreCase(typeId);

        return cacheService.getTileEntities(world, min, max, filter, limit);
    }

    @GET
    @Path("/{world}/{x}/{y}/{z}")
    @Permission("one")
    @ApiOperation(
            value = "Get tile entity",
            notes = "Get detailed information about a tile entity.")
    public CachedTileEntity getTileEntity(
            @PathParam("world") @ApiParam("The world the tile entity is in") CachedWorld world,
            @PathParam("x") @ApiParam("The x-coordinate of the tile-entity") Integer x,
            @PathParam("y") @ApiParam("The y-coordinate of the tile-entity") Integer y,
            @PathParam("z") @ApiParam("The z-coordinate of the tile-entity") Integer z)
            throws NotFoundException {
        Optional<CachedTileEntity> optTe = cacheService.getTileEntity(world, x, y, z);

        if (!optTe.isPresent()) {
            throw new NotFoundException("Tile entity in world '" + world.getName() +
                    "' at [" + x + "," + y + "," + z + "] could not be found");
        }

        return optTe.get();
    }

    @PUT
    @Path("/{world}/{x}/{y}/{z}")
    @Permission("modify")
    @ApiOperation(
            value = "Modify tile entity",
            notes = "Modify the properties of an existing tile entity.")
    public CachedTileEntity modifyTileEntity(
            @PathParam("world") @ApiParam("The world the tile entity is in") CachedWorld world,
            @PathParam("x") @ApiParam("The x-coordinate of the tile-entity") Integer x,
            @PathParam("y") @ApiParam("The y-coordinate of the tile-entity") Integer y,
            @PathParam("z") @ApiParam("The z-coordinate of the tile-entity") Integer z,
            UpdateTileEntityRequest req)
            throws NotFoundException, BadRequestException {

        if (req == null) {
            throw new BadRequestException("Request body is required");
        }

        Optional<CachedTileEntity> optTe = cacheService.getTileEntity(world, x, y, z);
        if (!optTe.isPresent()) {
            throw new NotFoundException("Tile entity in world '" + world.getName() +
                    "' at [" + x + "," + y + "," + z + "] could not be found");
        }

        return WebAPI.runOnMain(() -> {
            Optional<TileEntity> optLive = optTe.get().getLive();
            if (!optLive.isPresent())
                throw new InternalServerErrorException("Could not get live tile entity");

            TileEntity live = optLive.get();

            if (req.hasInventory()) {
                if (!(live instanceof TileEntityCarrier)) {
                    throw new BadRequestException("Tile entity does not have an inventory!");
                }

                try {
                    Inventory inv = ((TileEntityCarrier) live).getInventory();
                    inv.clear();
                    for (ItemStack stack : req.getInventory()) {
                        inv.offer(stack);
                    }
                } catch (Exception e) {
                    throw new InternalServerErrorException(e.getMessage());
                }
            }

            return new CachedTileEntity(live);
        });
    }

    @POST
    @Path("/{world}/{x}/{y}/{z}/method")
    @Permission("method")
    @ApiOperation(
            value = "Execute a method",
            notes = "Provides direct access to the underlaying tile entity object and can execute any method on it.")
    public ExecuteMethodResponse executeMethod(
            @PathParam("world") @ApiParam("The world the tile entity is in") CachedWorld world,
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

        Optional<CachedTileEntity> te = cacheService.getTileEntity(world, x, y, z);
        if (!te.isPresent()) {
            throw new NotFoundException("Tile entity in world '" + world.getName() +
                    "' at [" + x + "," + y + "," + z + "] could not be found");
        }

        String mName = req.getMethod();
        Tuple<Class[], Object[]> params = req.getParsedParameters();
        Object res = cacheService.executeMethod(te.get(), mName, params.getFirst(), params.getSecond());
        return new ExecuteMethodResponse(te.get(), res);
    }

    @ApiModel("UpdateTileEntityRequest")
    public static class UpdateTileEntityRequest {

        private List<ItemStack> inventory;
        @ApiModelProperty("The ItemStacks in the inventory of the tile entity")
        public List<ItemStack> getInventory() throws Exception {
            return inventory;
        }
        public boolean hasInventory() {
            return inventory != null;
        }
    }
}
