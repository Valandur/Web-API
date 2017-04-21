package valandur.webapi.servlets;

import com.fasterxml.jackson.databind.JsonNode;
import com.flowpowered.math.vector.Vector3i;
import org.apache.commons.lang3.math.NumberUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.cache.CachedWorld;
import valandur.webapi.cache.DataCache;
import valandur.webapi.misc.Permission;
import valandur.webapi.misc.Util;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

public class BlockServlet extends WebAPIServlet {

    public static int MAX_BLOCK_VOLUME_SIZE = 1000;

    @Override
    @Permission(perm = "block")
    protected void handleGet(ServletData data) {
        String[] parts = data.getPathParts();

        if (parts.length < 1 || parts[0].isEmpty()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid world UUID");
            return;
        }

        // Check world
        String uuid = parts[0];
        Optional<CachedWorld> world = DataCache.getWorld(UUID.fromString(uuid));
        if (!world.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "World with UUID '" + uuid + "' could not be found");
            return;
        }

        // Check min coordinates
        if (parts.length < 4 || !NumberUtils.isNumber(parts[1]) || !NumberUtils.isNumber(parts[2]) || !NumberUtils.isNumber(parts[3])) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid block coordinates / min coordinates");
            return;
        }

        int minX = Integer.parseInt(parts[1]);
        int minY = Integer.parseInt(parts[2]);
        int minZ = Integer.parseInt(parts[3]);

        int maxX = minX;
        int maxY = minY;
        int maxZ = minZ;

        if (parts.length > 4) {
            if (parts.length < 7 || !NumberUtils.isNumber(parts[4]) || !NumberUtils.isNumber(parts[5]) || !NumberUtils.isNumber(parts[6])) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid max coordinates");
                return;
            }

            maxX = Integer.parseInt(parts[4]);
            maxY = Integer.parseInt(parts[5]);
            maxZ = Integer.parseInt(parts[6]);
        }

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
        if (MAX_BLOCK_VOLUME_SIZE > 0 && numBlocks > MAX_BLOCK_VOLUME_SIZE) {
            data.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Size is " + numBlocks + " blocks, which is larger than the maximum of " + MAX_BLOCK_VOLUME_SIZE + " blocks");
            return;
        }

        if (numBlocks > 1) {
            JsonNode node = DataCache.getBlockVolume(world.get(), new Vector3i(minX, minY, minZ), new Vector3i(maxX, maxY, maxZ));
            data.addJson("volume", node);
        } else {
            JsonNode node = DataCache.getBlockAt(world.get(), new Vector3i(minX, minY, minZ));
            data.addJson("block", node);
        }
    }

    @Override
    @Permission(perm = "block")
    protected void handlePost(ServletData data) {
        JsonNode reqJson = (JsonNode) data.getAttribute("body");

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

            // Check world
            String uuid = wNode.asText();
            Optional<CachedWorld> world = DataCache.getWorld(UUID.fromString(uuid));
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
            if (MAX_BLOCK_VOLUME_SIZE > 0 && numBlocks > MAX_BLOCK_VOLUME_SIZE) {
                data.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Area size is " + numBlocks + " blocks, which is larger than the maximum of " + MAX_BLOCK_VOLUME_SIZE + " blocks");
                return;
            }

            JsonNode block = area.get("block");

            // If a property 'block' exists, then all blocks in the area are changed to the same type
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
                            Vector3i pos = new Vector3i(x, y, z);

                            Optional<Boolean> res = DataCache.setBlock(world.get(), pos, state);
                            if (res.isPresent() && res.get())
                                blocksSet++;
                        }
                    }
                }
            } else {
                // otherwise, the property 'blocks' defines nested arrays for every location in the area
                JsonNode blocks = area.get("blocks");

                if (blocks == null) {
                    data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Either 'block' or 'blocks' has to be defined on each area");
                    return;
                }

                for (int x = 0; x < size.getX(); x++) {
                    JsonNode xBlocks = blocks.get(x);

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

                            Vector3i pos = new Vector3i(min.getX() + x, min.getY() + y, min.getZ() + z);

                            try {
                                BlockState state = parseBlockState(b);

                                Optional<Boolean> res = DataCache.setBlock(world.get(), pos, state);
                                if (res.isPresent() && res.get())
                                    blocksSet++;
                            } catch (Exception e) {
                                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not process block state: " + e.getMessage());
                                return;
                            }
                        }
                    }
                }
            }
        }

        data.addJson("blocksChanged", blocksSet);
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
