package valandur.webapi.serialize.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class BlockStateDeserializer extends StdDeserializer<BlockState> {

    public BlockStateDeserializer() {
        super(BlockState.class);
    }

    @Override
    public BlockState deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode root = p.readValueAsTree();
        if (root.path("type").isMissingNode()) {
            throw new IOException("Missing block type");
        }

        String typeStr = root.path("type").isTextual()
                ? root.path("type").asText()
                : root.path("type").path("id").asText();
        Optional<BlockType> optType = Sponge.getRegistry().getType(BlockType.class, typeStr);
        if (!optType.isPresent()) {
            throw new IOException("Invalid block type " + typeStr);
        }
        BlockType type = optType.get();

        BlockState state = type.getDefaultState();
        Collection<BlockTrait<?>> traits = type.getTraits();

        if (!root.path("data").isMissingNode()) {
            Iterator<Map.Entry<String, JsonNode>> it = root.path("data").fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = it.next();

                Optional<BlockTrait<?>> optTrait = traits.stream().filter(t ->
                        t.getName().equalsIgnoreCase(entry.getKey())
                ).findAny();

                if (!optTrait.isPresent())
                    throw new IOException("Unknown trait '" + entry.getKey() + "'");

                BlockTrait trait = optTrait.get();
                Object value = null;

                JsonNode nodeValue = entry.getValue();
                if (nodeValue.isBoolean()) {
                    value = nodeValue.asBoolean();
                } else if (nodeValue.isInt()) {
                    value = nodeValue.asInt();
                } else if (nodeValue.isTextual()) {
                    Collection<?> values = trait.getPossibleValues();
                    Optional<?> val = values.stream()
                            .filter(v -> v.toString().equalsIgnoreCase(nodeValue.asText()))
                            .findAny();

                    if (!val.isPresent()) {
                        String allowedValues = values.stream()
                                .map(Object::toString)
                                .collect(Collectors.joining(", "));
                        throw new IOException("Trait '" + trait.getName() + "' has value '" +
                                nodeValue.asText() + "' but can only have one of: " + allowedValues);
                    } else {
                        value = val.get();
                    }
                }

                Optional<BlockState> newState = state.withTrait(trait, value);
                if (!newState.isPresent())
                    throw new IOException("Could not apply trait '" + trait.getName() + " to block state");

                state = newState.get();
            }
        }

        return state;
    }
}
