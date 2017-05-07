package valandur.webapi.blocks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.CachedWorld;
import valandur.webapi.json.JsonConverter;

import java.util.*;

public class Blocks {
    private static Map<UUID, BlockUpdate> blockUpdates = new HashMap<>();

    public static int MAX_BLOCK_GET_SIZE = 1000;
    public static int MAX_BLOCK_UPDATE_SIZE = 100;

    public static UUID startBlockUpdate(UUID worldId, List<Tuple<Vector3i, BlockState>> blocks) {
        BlockUpdate update = new BlockUpdate(worldId, blocks);
        blockUpdates.put(update.getUUID(), update);
        update.start();
        return update.getUUID();
    }

    public static Collection<BlockUpdate> getBlockUpdates() {
        return blockUpdates.values();
    }
    public static Optional<BlockUpdate> getBlockUpdate(UUID uuid) {
        if (!blockUpdates.containsKey(uuid))
            return Optional.empty();
        return Optional.of(blockUpdates.get(uuid));
    }

    public static JsonNode getBlockVolume(CachedWorld world, Vector3i min, Vector3i max) {
        Optional<JsonNode> node = WebAPI.runOnMain(() -> {
            Optional<?> obj = world.getLive();

            if (!obj.isPresent())
                return null;

            World w = (World)obj.get();
            return JsonConverter.toJson(w.getBlockView(min, max));
        });

        return node.orElseGet(JsonNodeFactory.instance::nullNode);
    }
    public static JsonNode getBlockAt(CachedWorld world, Vector3i pos) {
        Optional<JsonNode> node = WebAPI.runOnMain(() -> {
            Optional<?> obj = world.getLive();

            if (!obj.isPresent())
                return null;

            World w = (World)obj.get();
            return JsonConverter.toJson(w.getBlock(pos));
        });

        return node.orElseGet(JsonNodeFactory.instance::nullNode);
    }
}
