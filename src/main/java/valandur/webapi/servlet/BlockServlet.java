package valandur.webapi.servlet;

import com.flowpowered.math.vector.Vector3i;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.spongepowered.api.block.BlockState;
import valandur.webapi.WebAPI;
import valandur.webapi.api.block.IBlockOperation;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.ExplicitDetails;
import valandur.webapi.api.servlet.Permission;
import valandur.webapi.block.BlockChangeOperation;
import valandur.webapi.block.BlockGetOperation;
import valandur.webapi.serialize.view.block.BlockStateView;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

@Path("block")
@Api(value = "block", tags = { "Block" })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class BlockServlet extends BaseServlet {

    @GET
    @Path("/{world}/{x}/{y}/{z}")
    @Permission("one")
    @ApiOperation(value = "Get a block", notes = "Gets information about one block in the world.")
    public BlockStateView getBlock(
            @PathParam("world") @ApiParam("The uuid of the world to get the block from") ICachedWorld world,
            @PathParam("x") @ApiParam("The x-coordinate of the block") int x,
            @PathParam("y") @ApiParam("The y-coordinate of the block") int y,
            @PathParam("z") @ApiParam("The z-coordinate of the block") int z)
            throws InternalServerErrorException {
        Vector3i pos = new Vector3i(x, y, z);
        return new BlockStateView(blockService.getBlockAt(world, pos));
    }

    @GET
    @Path("/op")
    @ExplicitDetails
    @Permission({ "op", "list" })
    @ApiOperation(value = "List block operations",
            notes = "Returns a list of all the currently running block operations.")
    public Collection<IBlockOperation> getBlockOperations() {
        return blockService.getBlockOperations();
    }

    @POST
    @Path("/op")
    @Permission({ "op", "create" })
    @ApiOperation(value = "Create a block operation",
            notes = "Start a request to get or change blocks on the server.")
    public IBlockOperation createBlockOperation(CreateOperationRequest req)
            throws BadRequestException, NotAcceptableException {
        // Check world
        if (!req.getWorld().isPresent()) {
            throw new BadRequestException("No valid world provided");
        }

        // Check min
        if (req.getMin() == null) {
            throw new BadRequestException("Min coordinates missing");
        }

        // Check max
        if (req.getMax() == null) {
            throw new BadRequestException("Max coordinates missing");
        }

        // Swap around min & max if needed
        Vector3i min = req.getMin().min(req.getMax());
        Vector3i max = req.getMax().max(req.getMin());

        // Calculate volume size
        Vector3i size = max.sub(min).add(1, 1, 1);
        int numBlocks = size.getX() * size.getY() * size.getZ();

        IBlockOperation op;
        if (req.getType() == IBlockOperation.BlockOperationType.GET) {
            // Check volume size
            if (blockService.getMaxGetBlocks() > 0 && numBlocks > blockService.getMaxGetBlocks()) {
                throw new NotAcceptableException("Size is " + numBlocks +
                        " blocks, which is larger than the maximum of " +
                        blockService.getMaxGetBlocks() + " blocks");
            }

            op = blockService.startBlockOperation(new BlockGetOperation(req.getWorld().get(), min, max));
        } else if (req.getType() == IBlockOperation.BlockOperationType.CHANGE) {
            // Check volume size
            if (blockService.getMaxUpdateBlocks() > 0 && numBlocks > blockService.getMaxUpdateBlocks()) {
                throw new NotAcceptableException("Size is " + numBlocks +
                        " blocks, which is larger than the maximum of " +
                        blockService.getMaxUpdateBlocks() + " blocks");
            }

            // Collect a list of blocks we want to update
            Map<Vector3i, BlockState> blocks = new HashMap<>();

            if (req.getBlock() != null) {
                try {
                    BlockState state = req.getBlock();

                    for (int x = min.getX(); x <= max.getX(); x++) {
                        for (int y = min.getY(); y <= max.getY(); y++) {
                            for (int z = min.getZ(); z <= max.getZ(); z++) {
                                blocks.put(new Vector3i(x, y, z), state);
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new BadRequestException("Could not process block state: " + e.getMessage());
                }
            } else {
                if (req.getBlocks() == null) {
                    throw new BadRequestException("Either 'block' or 'blocks' has to be defined on each area");
                }

                for (int x = 0; x < size.getX(); x++) {
                    BlockState[][] xBlocks = req.getBlocks()[x];

                    if (xBlocks == null)
                        continue;

                    for (int y = 0; y < size.getY(); y++) {
                        BlockState[] yBlocks = xBlocks[y];

                        if (yBlocks == null)
                            continue;

                        for (int z = 0; z < size.getZ(); z++) {
                            BlockState block = yBlocks[z];

                            if (block == null)
                                continue;

                            try {
                                blocks.put(new Vector3i(min.getX() + x, min.getY() + y, min.getZ() + z), block);
                            } catch (Exception e) {
                                throw new BadRequestException("Could not process block state: " + e.getMessage());
                            }
                        }
                    }
                }
            }

            op = blockService.startBlockOperation(new BlockChangeOperation(req.getWorld().get(), min, max, blocks));
        } else {
            throw new BadRequestException("Unknown block operation type");
        }

        return op;
    }

    @GET
    @Path("/op/{uuid}")
    @Permission({ "op", "one" })
    @ApiOperation(value = "Get a block operation", notes = "Gets details about a specific block operation")
    public IBlockOperation getBlockOperation(
            @PathParam("uuid") @ApiParam("The uuid of the block operation") UUID uuid)
            throws NotFoundException {
        // Check block op
        Optional<IBlockOperation> op = blockService.getBlockOperation(uuid);
        if (!op.isPresent()) {
            throw new NotFoundException("Block operation with UUID '" + uuid + "' could not be found");
        }

        return op.get();
    }

    @PUT
    @Path("/op/{uuid}")
    @Permission({ "op", "change" })
    @ApiOperation(value = "Change a block operation",
            notes = "Modify an existing block operation to either pause or continue it.")
    public IBlockOperation modifyBlockOperation(
            @PathParam("uuid") @ApiParam("The uuid of the block operation") UUID uuid)
            throws NotFoundException {
        // Check block op
        Optional<IBlockOperation> op = blockService.getBlockOperation(uuid);
        if (!op.isPresent()) {
            throw new NotFoundException("Block opeartion with UUID '" + uuid + "' could not be found");
        }

        // TODO: Implement pausing and unpausing block ops
        /*if (reqJson.get("pause").asBoolean()) {
            op.get().pause();
        } else {
            op.get().start();
        }*/

        return op.get();
    }

    @DELETE
    @Path("/op/{uuid}")
    @Permission({ "op", "delete" })
    @ApiOperation(value = "Stop a block operation",
            notes = "Cancel a pending or running block operation. **THIS DOES NOT UNDO THE BLOCK CHANGES**")
    public IBlockOperation deleteBlockOperation(
            @PathParam("uuid") @ApiParam("The uuid of the block operation") UUID uuid)
            throws NotFoundException {
        // Check block op
        Optional<IBlockOperation> op = blockService.getBlockOperation(uuid);
        if (!op.isPresent()) {
            throw new NotFoundException("Block operation with UUID '" + uuid + "' could not be found");
        }

        op.get().stop(null);

        return op.get();
    }


    public static class CreateOperationRequest {

        private IBlockOperation.BlockOperationType type;
        public IBlockOperation.BlockOperationType getType() {
            return type;
        }

        private String world;
        public Optional<ICachedWorld> getWorld() {
            return WebAPI.getCacheService().getWorld(world);
        }

        private Vector3i min;
        public Vector3i getMin() {
            return min;
        }

        private Vector3i max;
        public Vector3i getMax() {
            return max;
        }

        private BlockState block;
        public BlockState getBlock() {
            return block;
        }

        private BlockState[][][] blocks;
        public BlockState[][][] getBlocks() {
            return blocks;
        }
    }
}
