package valandur.webapi.servlet.block;

import com.fasterxml.jackson.databind.JsonNode;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.extent.BlockVolume;
import valandur.webapi.WebAPI;
import valandur.webapi.api.annotation.WebAPIRoute;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.block.IBlockUpdate;
import valandur.webapi.api.cache.world.CachedWorld;
import valandur.webapi.api.servlet.WebAPIBaseServlet;
import valandur.webapi.services.BlockService;
import valandur.webapi.servlet.ServletData;
import valandur.webapi.util.Util;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@WebAPIServlet(basePath = "block")
public class BlockServlet extends WebAPIBaseServlet {

    @WebAPIRoute(method = "GET", path = "/:world/:x/:y/:z", perm = "one")
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

    @WebAPIRoute(method = "GET", path = "/:world/:minX/:minY/:minZ/:maxX/:maxY/:maxZ", perm = "volume")
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
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Could not get world");
            return;
        }

        data.addJson("ok", true, false);
        data.addJson("volume", vol.get(), true);
    }

    @WebAPIRoute(method = "POST", path = "/", perm = "set")
    public void setBlocks(ServletData data) {
        JsonNode reqJson = data.getRequestBody();

        if (reqJson == null || !reqJson.isArray()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid areas");
            return;
        }

        int blocksSet = 0;

        // Process areas
        for (JsonNode area : reqJson) {
            JsonNode wNode = area.get("world");

            if (wNode == null || !wNode.isTextual()) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid world UUID");
                return;
            }

            // Check uuid
            String uuid = wNode.asText();
            if (!Util.isValidUUID(uuid)) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid world UUID");
                return;
            }

            // Check world
            Optional<CachedWorld> world = WebAPI.getCacheService().getWorld(UUID.fromString(uuid));
            if (!world.isPresent()) {
                data.sendError(HttpServletResponse.SC_NOT_FOUND, "World with UUID '" + uuid + "' could not be found");
                return;
            }

            // Check min & max
            Optional<Vector3i> minOpt = getVector3i(area, "min");
            Optional<Vector3i> maxOpt = getVector3i(area, "max");
            if (!minOpt.isPresent() || !maxOpt.isPresent()) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Area needs to define 'min' and 'max' properties");
                return;
            }

            Vector3i min = minOpt.get();
            Vector3i max = maxOpt.get();

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

            // If a property 'block' exists, then all blocks in the area are changed to the same type
            JsonNode block = area.get("block");
            if (block != null) {
                BlockState state = null;
                try {
                    state = parseBlockState(block);
                } catch (Exception e) {
                    data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not process block state: " + e.getMessage());
                    continue;
                }

                for (int x = min.getX(); x <= max.getX(); x++) {
                    for (int y = min.getY(); y <= max.getY(); y++) {
                        for (int z = min.getZ(); z <= max.getZ(); z++) {
                            blocks.add(new Tuple<>(new Vector3i(x, y, z), state));
                        }
                    }
                }
            } else {
                // otherwise, the property 'blocks' defines nested arrays for every location in the area
                JsonNode jsonBlocks = area.get("blocks");

                if (jsonBlocks == null) {
                    data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Either 'block' or 'blocks' has to be defined on each area");
                    return;
                }

                for (int x = 0; x < size.getX(); x++) {
                    JsonNode xBlocks = jsonBlocks.get(x);

                    if (xBlocks == null)
                        continue;

                    for (int y = 0; y < size.getY(); y++) {
                        JsonNode yBlocks = xBlocks.get(y);

                        if (yBlocks == null)
                            continue;

                        for (int z = 0; z < size.getZ(); z++) {
                            JsonNode b = yBlocks.get(z);

                            if (b == null)
                                continue;

                            try {
                                BlockState state = parseBlockState(b);
                                blocks.add(new Tuple<>(new Vector3i(min.getX() + x, min.getY() + y, min.getZ() + z), state));
                            } catch (Exception e) {
                                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not process block state: " + e.getMessage());
                                return;
                            }
                        }
                    }
                }
            }

            IBlockUpdate update = blockService.startBlockUpdate(world.get().getUUID(), blocks);
            data.addJson("ok", true, false);
            data.addJson("update", update, true);
        }
    }

    @WebAPIRoute(method = "GET", path = "/update", perm = "update.list")
    public void getBlockUpdates(ServletData data) {
        data.addJson("ok", true, false);
        data.addJson("updates", blockService.getBlockUpdates(), data.getQueryParam("details").isPresent());
    }

    @WebAPIRoute(method = "PUT", path = "/update/:uuid", perm = "update.change")
    public void modifyBlockUpdate(ServletData data, UUID uuid) {
        JsonNode reqJson = data.getRequestBody();

        // Check block update
        Optional<IBlockUpdate> update = blockService.getBlockUpdate(uuid);
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

    @WebAPIRoute(method = "DELETE", path = "/update/:uuid", perm = "update.delete")
    public void deleteBlockUpdate(ServletData data, UUID uuid) {
        // Check block update
        Optional<IBlockUpdate> update = blockService.getBlockUpdate(uuid);
        if (!update.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Block update with UUID '" + uuid + "' could not be found");
            return;
        }

        update.get().stop("Cancelled by request");

        data.addJson("ok", true, false);
        data.addJson("update", update, true);
    }

    private Optional<Vector3i> getVector3i(JsonNode rootNode, String name) {
        JsonNode node = rootNode.get(name);
        if (node == null)
            return Optional.empty();

        JsonNode xNode = node.get("x");
        if (xNode == null)
            return Optional.empty();

        JsonNode yNode = node.get("y");
        if (yNode == null)
            return Optional.empty();

        JsonNode zNode = node.get("z");
        if (zNode == null)
            return Optional.empty();

        return Optional.of(new Vector3i(xNode.asInt(), yNode.asInt(), zNode.asInt()));
    }

    private Optional<BlockType> parseBlockType(String type) {
        return  Sponge.getRegistry().getType(BlockType.class, type);
    }
    private BlockState parseBlockState(JsonNode node) throws Exception {
        String typeStr = node.get("type").asText();
        Optional<BlockType> type = parseBlockType(typeStr);
        if (!type.isPresent())
            throw new Exception("Invalid block type '" + typeStr + "'");

        BlockState state = type.get().getDefaultState();

        List<Tuple<BlockTrait, Object>> traits = parseBlockTraits(type.get(), node.get("data"));
        for (Tuple<BlockTrait, Object> trait : traits) {
            Optional<BlockState> newState = state.withTrait(trait.getFirst(), trait.getSecond());
            if (!newState.isPresent())
                throw new Exception("Could not apply trait '" + trait.getFirst().getName() + " to block state");

            state = newState.get();
        }

        return state;
    }
    private List<Tuple<BlockTrait, Object>> parseBlockTraits(BlockType type, JsonNode data) throws Exception {
        List<Tuple<BlockTrait, Object>> list = new ArrayList<>();
        Iterator<Map.Entry<String, JsonNode>> nodes = data.fields();

        Collection<BlockTrait<?>> traits = type.getTraits();

        while (nodes.hasNext()) {
            Map.Entry<String, JsonNode> entry = nodes.next();

            Optional<BlockTrait<?>> optTrait = traits.stream().filter(t -> t.getName().equalsIgnoreCase(entry.getKey())).findAny();
            if (!optTrait.isPresent())
                throw new Exception("Unknown trait '" + entry.getKey() + "'");

            BlockTrait trait = optTrait.get();

            JsonNode value = entry.getValue();
            if (value.isBoolean()) {
                list.add(new Tuple<>(trait, value.asBoolean()));
            } else if (value.isInt()) {
                list.add(new Tuple<>(trait, value.asInt()));
            } else if (value.isTextual()) {
                Collection<?> values = trait.getPossibleValues();
                Optional<?> val = values.stream().filter(v -> v.toString().equalsIgnoreCase(value.asText())).findAny();
                if (!val.isPresent()) {
                    String allowedValues = values.stream().map(Object::toString).collect(Collectors.joining(", "));
                    throw new Exception("Trait '" + trait.getName() + "' has value '" + value.asText() + "' but can only have one of: " + allowedValues);
                } else {
                    list.add(new Tuple<>(trait, val.get()));
                }
            }
        }

        return list;
    }
}
