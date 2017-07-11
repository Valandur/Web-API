package valandur.webapi.servlet.block;

import com.fasterxml.jackson.databind.JsonNode;
import com.flowpowered.math.vector.Vector3i;
import org.eclipse.jetty.http.HttpMethod;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.extent.BlockVolume;
import valandur.webapi.api.annotation.WebAPIEndpoint;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.block.IBlockOperation;
import valandur.webapi.api.servlet.WebAPIBaseServlet;
import valandur.webapi.block.BlockService;
import valandur.webapi.cache.world.CachedWorld;
import valandur.webapi.servlet.ServletData;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static valandur.webapi.servlet.block.BlockUpdateRequest.BlockStateRequest;

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

    @WebAPIEndpoint(method = HttpMethod.GET, path = "/:world/:minX/:minY/:minZ/:maxX/:maxY/:maxZ", perm = "volume")
    public void getBlockVolume(ServletData data, CachedWorld world, int minX, int minY, int minZ,
                               int maxX, int maxY, int maxZ) {
        // Swap around min & max if needed
        int tmpX = Math.max(minX, maxX);
        minX = Math.min(minX, maxX);
        maxX = tmpX;

        // Swap around min & max if needed
        int tmpY = Math.max(minY, maxY);
        minY = Math.min(minY, maxY);
        maxY = tmpY;

        // Swap around min & max if needed
        int tmpZ = Math.max(minZ, maxZ);
        minZ = Math.min(minZ, maxZ);
        maxZ = tmpZ;

        // Check volume size
        int numBlocks = Math.abs((1 + maxX - minX) * (1 + maxY - minY) * (1 + maxZ - minZ));
        if (BlockService.MAX_BLOCK_GET_SIZE > 0 && numBlocks > BlockService.MAX_BLOCK_GET_SIZE) {
            data.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Size is " + numBlocks +
                    " blocks, which is larger than the maximum of " + BlockService.MAX_BLOCK_GET_SIZE + " blocks");
            return;
        }

        Vector3i min = new Vector3i(minX, minY, minZ);
        Vector3i max = new Vector3i(maxX, maxY, maxZ);
        Optional<BlockVolume> vol = blockService.getBlockVolume(world, min, max);
        if (!vol.isPresent()) {
            data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Could not get blocks. Is the world loaded? Does the server have enough memory?");
            return;
        }

        data.addJson("ok", true, false);
        data.addJson("volume", vol.get(), true);
    }

    @WebAPIEndpoint(method = HttpMethod.POST, path = "/", perm = "set")
    public void setBlocks(ServletData data) {
        JsonNode reqJson = data.getRequestBody();

        if (reqJson == null || !reqJson.isArray()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid areas");
            return;
        }

        int blocksSet = 0;

        // Process areas
        List<BlockUpdateRequest> reqs = new ArrayList<>();

        if (!reqJson.isArray()) {
            Optional<BlockUpdateRequest> optReq = data.getRequestBody(BlockUpdateRequest.class);
            if (!optReq.isPresent()) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid block update data: " + data.getLastParseError().getMessage());
                return;
            }

            reqs.add(optReq.get());
        } else {
            Optional<BlockUpdateRequest[]> optReqs = data.getRequestBody(BlockUpdateRequest[].class);
            if (!optReqs.isPresent()) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid block update data: " + data.getLastParseError().getMessage());
                return;
            }

            reqs = Arrays.asList(optReqs.get());
        }

        List<IBlockOperation> updates = new ArrayList<>();
        for (BlockUpdateRequest req : reqs) {
            if (!req.getWorld().isPresent()) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "No valid world provided");
                return;
            }

            if (req.getMin() == null) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Min coordinates missing");
                return;
            }

            if (req.getMax() == null) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Max coordinates missing");
                return;
            }

            Vector3i min = req.getMin();
            Vector3i max = req.getMax();

            // Swamp around min & max if needed
            Vector3i tmp = min.min(max);
            max = min.max(max);
            min = tmp;

            // Check volume size
            Vector3i size = max.sub(min).add(1, 1, 1);

            int numBlocks = size.getX() * size.getY() * size.getZ();
            if (BlockService.MAX_BLOCK_UPDATE_SIZE > 0 && numBlocks > BlockService.MAX_BLOCK_UPDATE_SIZE) {
                data.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Area size is " + numBlocks +
                        " blocks, which is larger than the maximum of " + BlockService.MAX_BLOCK_UPDATE_SIZE + " blocks");
                return;
            }

            // Collect a list of blocks we want to update
            List<Tuple<Vector3i, BlockState>> blocks = new ArrayList<>();

            if (req.getBlock() != null) {
                try {
                    BlockState state = req.getBlock().getState();

                    for (int x = min.getX(); x <= max.getX(); x++) {
                        for (int y = min.getY(); y <= max.getY(); y++) {
                            for (int z = min.getZ(); z <= max.getZ(); z++) {
                                blocks.add(new Tuple<>(new Vector3i(x, y, z), state));
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
                                blocks.add(new Tuple<>(new Vector3i(min.getX() + x, min.getY() + y, min.getZ() + z), state));
                            } catch (Exception e) {
                                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not process block state: " + e.getMessage());
                                return;
                            }
                        }
                    }
                }
            }

            IBlockOperation update = blockService.startBlockOperation(req.getWorld().get().getUUID(), blocks);
            updates.add(update);
        }

        data.addJson("ok", true, false);
        data.addJson("update", updates, true);
    }

    @WebAPIEndpoint(method = HttpMethod.GET, path = "/update", perm = "update.list")
    public void getBlockUpdates(ServletData data) {
        data.addJson("ok", true, false);
        data.addJson("updates", blockService.getBlockOperations(), data.getQueryParam("details").isPresent());
    }

    @WebAPIEndpoint(method = HttpMethod.PUT, path = "/update/:uuid", perm = "update.change")
    public void modifyBlockUpdate(ServletData data, UUID uuid) {
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
        data.addJson("update", update, true);
    }

    @WebAPIEndpoint(method = HttpMethod.DELETE, path = "/update/:uuid", perm = "update.delete")
    public void deleteBlockUpdate(ServletData data, UUID uuid) {
        // Check block update
        Optional<IBlockOperation> update = blockService.getBlockOperation(uuid);
        if (!update.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Block update with UUID '" + uuid + "' could not be found");
            return;
        }

        update.get().stop("Cancelled by request");

        data.addJson("ok", true, false);
        data.addJson("update", update, true);
    }
}
