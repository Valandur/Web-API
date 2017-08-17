package valandur.webapi.servlet.block;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.world.ICachedWorld;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CreateOperationRequest {

    public static enum BlockOperationType {
        GET, CHANGE,
    }

    @JsonDeserialize
    private BlockOperationType type;
    public BlockOperationType getType() {
        return type;
    }

    @JsonDeserialize
    private String world;
    public Optional<ICachedWorld> getWorld() {
        return WebAPI.getCacheService().getWorld(world);
    }

    @JsonDeserialize
    private Vector3i min;
    public Vector3i getMin() {
        return min;
    }

    @JsonDeserialize
    private Vector3i max;
    public Vector3i getMax() {
        return max;
    }

    @JsonDeserialize
    private BlockStateRequest block;
    public BlockStateRequest getBlock() {
        return block;
    }

    @JsonDeserialize
    private BlockStateRequest[][][] blocks;
    public BlockStateRequest[][][] getBlocks() {
        return blocks;
    }


    public static class BlockStateRequest {

        @JsonDeserialize
        private String type;
        public Optional<BlockType> getType() {
            return Sponge.getRegistry().getType(BlockType.class, type);
        }

        @JsonDeserialize
        private Map<String, JsonNode> data;
        public Map<String, JsonNode> getData() {
            return data;
        }


        public BlockState getState() throws Exception {
            Optional<BlockType> optType = getType();
            if (!optType.isPresent())
                throw new Exception("Invalid block type");

            BlockType type = optType.get();

            BlockState state = type.getDefaultState();
            Collection<BlockTrait<?>> traits = type.getTraits();

            if (data != null) {
                for (Map.Entry<String, JsonNode> entry : data.entrySet()) {
                    Optional<BlockTrait<?>> optTrait = traits.stream().filter(t ->
                            t.getName().equalsIgnoreCase(entry.getKey())
                    ).findAny();

                    if (!optTrait.isPresent())
                        throw new Exception("Unknown trait '" + entry.getKey() + "'");

                    BlockTrait trait = optTrait.get();
                    Object value = null;

                    JsonNode nodeValue = entry.getValue();
                    if (nodeValue.isBoolean()) {
                        value = nodeValue.asBoolean();
                    } else if (nodeValue.isInt()) {
                        value = nodeValue.asInt();
                    } else if (nodeValue.isTextual()) {
                        Collection<?> values = trait.getPossibleValues();
                        Optional<?> val = values.stream().filter(v -> v.toString().equalsIgnoreCase(nodeValue.asText())).findAny();
                        if (!val.isPresent()) {
                            String allowedValues = values.stream().map(Object::toString).collect(Collectors.joining(", "));
                            throw new Exception("Trait '" + trait.getName() + "' has value '" + nodeValue.asText() + "' but can only have one of: " + allowedValues);
                        } else {
                            value = val.get();
                        }
                    }

                    Optional<BlockState> newState = state.withTrait(trait, value);
                    if (!newState.isPresent())
                        throw new Exception("Could not apply trait '" + trait.getName() + " to block state");

                    state = newState.get();
                }
            }

            return state;
        }
    }
}
