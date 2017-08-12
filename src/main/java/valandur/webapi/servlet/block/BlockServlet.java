package valandur.webapi.servlet.block;

import com.fasterxml.jackson.databind.JsonNode;
import com.flowpowered.math.vector.Vector3i;
import org.eclipse.jetty.http.HttpMethod;
import org.spongepowered.api.block.BlockState;
import valandur.webapi.api.annotation.WebAPIEndpoint;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.block.IBlockOperation;
import valandur.webapi.api.servlet.WebAPIBaseServlet;
import valandur.webapi.block.BlockChangeOperation;
import valandur.webapi.block.BlockGetOperation;
import valandur.webapi.cache.world.CachedWorld;
import valandur.webapi.servlet.ServletData;
import valandur.webapi.servlet.block.CreateOperationRequest.BlockOperationType;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static valandur.webapi.servlet.block.CreateOperationRequest.BlockStateRequest;

@WebAPIServlet(basePath = "block")
public class BlockServlet extends WebAPIBaseServlet {

    @WebAPIEndpoint(method = HttpMethod.GET, path = "/:world/:x/:y/:z", perm = "one")
    public void getBlock(ServletData data, CachedWorld world, int x, int y, int z) {
        Vector3i pos = new Vector3i(x, y, z);
        Optional<BlockState> state = blockService.getBlockAt(world, pos);
        if (!state.isPresent()) {
            data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not get world " + world.getName());
            return;
        }

        data.addJson("ok", true, false);
        data.addJson("position", pos, false);
        data.addJson("block", state.get(), true);
    }

    @WebAPIEndpoint(method = HttpMethod.GET, path = "/op", perm = "op.list")
    public void getBlockOperations(ServletData data) {
        data.addJson("ok", true, false);
        data.addJson("operations", blockService.getBlockOperations(), data.getQueryParam("details").isPresent());
    }

    @WebAPIEndpoint(method = HttpMethod.POST, path = "/op", perm = "op.create")
    public void createBlockOperation(ServletData data) {
        Optional<CreateOperationRequest> optReq = data.getRequestBody(CreateOperationRequest.class);
        if (!optReq.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid block data: " + data.getLastParseError().getMessage());
            return;
        }

        CreateOperationRequest req = optReq.get();

        // Check world
        if (!req.getWorld().isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "No valid world provided");
            return;
        }

        // Check min
        if (req.getMin() == null) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Min coordinates missing");
            return;
        }

        // Check max
        if (req.getMax() == null) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Max coordinates missing");
            return;
        }

        // Swap around min & max if needed
        Vector3i min = req.getMin().min(req.getMax());
        Vector3i max = req.getMax().max(req.getMin());

        // Calculate volume size
        Vector3i size = max.sub(min).add(1, 1, 1);
        int numBlocks = size.getX() * size.getY() * size.getZ();

        IBlockOperation op = null;
        if (req.getType() == BlockOperationType.GET) {
            // Check volume size
            if (blockService.getMaxGetBlocks() > 0 && numBlocks > blockService.getMaxGetBlocks()) {
                data.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Size is " + numBlocks +
                        " blocks, which is larger than the maximum of " + blockService.getMaxGetBlocks() + " blocks");
                return;
            }

            op = blockService.startBlockOperation(new BlockGetOperation(req.getWorld().get(), min, max));
        } else if (req.getType() == BlockOperationType.CHANGE) {
            // Check volume size
            if (blockService.getMaxUpdateBlocks() > 0 && numBlocks > blockService.getMaxUpdateBlocks()) {
                data.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Area size is " + numBlocks +
                        " blocks, which is larger than the maximum of " + blockService.getMaxUpdateBlocks() + " blocks");
                return;
            }

            // Collect a list of blocks we want to update
            Map<Vector3i, BlockState> blocks = new HashMap<>();

            if (req.getBlock() != null) {
                try {
                    BlockState state = req.getBlock().getState();

                    for (int x = min.getX(); x <= max.getX(); x++) {
                        for (int y = min.getY(); y <= max.getY(); y++) {
                            for (int z = min.getZ(); z <= max.getZ(); z++) {
                                blocks.put(new Vector3i(x, y, z), state);
                            }
                        }
                    }
                } catch (Exception e) {
                    data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not process block state: " + e.getMessage());
                    return;
                }
            } else {
                if (req.getBlocks() == null) {
                    data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Either 'block' or 'blocks' has to be defined on each area");
                    return;
                }

                for (int x = 0; x < size.getX(); x++) {
                    BlockStateRequest[][] xBlocks = req.getBlocks()[x];

                    if (xBlocks == null)
                        continue;

                    for (int y = 0; y < size.getY(); y++) {
                        BlockStateRequest[] yBlocks = xBlocks[y];

                        if (yBlocks == null)
                            continue;

                        for (int z = 0; z < size.getZ(); z++) {
                            BlockStateRequest block = yBlocks[z];

                            if (block == null)
                                continue;

                            try {
                                BlockState state = block.getState();
                                blocks.put(new Vector3i(min.getX() + x, min.getY() + y, min.getZ() + z), state);
                            } catch (Exception e) {
                                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not process block state: " + e.getMessage());
                                return;
                            }
                        }
                    }
                }
            }

            op = blockService.startBlockOperation(new BlockChangeOperation(req.getWorld().get(), min, max, blocks));
        }

        data.addJson("ok", true, false);
        data.addJson("operation", op, false);
    }

    @WebAPIEndpoint(method = HttpMethod.GET, path = "/op/:uuid", perm = "op.one")
    public void getBlockOperation(ServletData data, UUID uuid) {
        // Check block update
        Optional<IBlockOperation> update = blockService.getBlockOperation(uuid);
        if (!update.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Block update with UUID '" + uuid + "' could not be found");
            return;
        }

        data.addJson("ok", true, false);
        data.addJson("operation", update.get(), true);
    }

    @WebAPIEndpoint(method = HttpMethod.PUT, path = "/op/:uuid", perm = "op.change")
    public void modifyBlockOperation(ServletData data, UUID uuid) {
        JsonNode reqJson = data.getRequestBody();

        // Check block update
        Optional<IBlockOperation> update = blockService.getBlockOperation(uuid);
        if (!update.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Block update with UUID '" + uuid + "' could not be found");
            return;
        }

        if (reqJson.get("pause").asBoolean()) {
            update.get().pause();
        } else {
            update.get().start();
        }

        data.addJson("ok", true, false);
        data.addJson("operation", update, true);
    }

    @WebAPIEndpoint(method = HttpMethod.DELETE, path = "/op/:uuid", perm = "op.delete")
    public void deleteBlockOperation(ServletData data, UUID uuid) {
        // Check block update
        Optional<IBlockOperation> update = blockService.getBlockOperation(uuid);
        if (!update.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Block update with UUID '" + uuid + "' could not be found");
            return;
        }

        update.get().stop("Cancelled by request");

        data.addJson("ok", true, false);
        data.addJson("operation", update.get(), true);
    }
}
