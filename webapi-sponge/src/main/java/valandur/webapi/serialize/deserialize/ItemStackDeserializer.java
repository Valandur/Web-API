package valandur.webapi.serialize.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.WebAPI;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class ItemStackDeserializer extends StdDeserializer<ItemStack> {

    public ItemStackDeserializer() {
        super(ItemStack.class);
    }

    @Override
    public ItemStack deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode root = p.readValueAsTree();
        if (root.path("type").isMissingNode())
            throw new IOException("Missing item type");

        String id = root.path("type").isTextual()
                ? root.path("type").asText()
                : root.path("type").path("id").asText();
        Optional<ItemType> optType = Sponge.getRegistry().getType(ItemType.class, id);
        if (!optType.isPresent())
            throw new IOException("Invalid item type " + id);

        Integer amount = root.path("quantity").isMissingNode() ? 1 : root.path("quantity").asInt();

        ItemType type = optType.get();

        ItemStack.Builder builder = ItemStack.builder().itemType(type).quantity(amount);
        ItemStack item = builder.build();

        if (!root.path("data").isMissingNode()) {
            Iterator<Map.Entry<String, JsonNode>> it = root.path("data").fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = it.next();
                Class<? extends DataManipulator> c = WebAPI.getSerializeService().getSupportedData().get(entry.getKey());
                if (c == null) continue;
                Optional<? extends DataManipulator> optData = item.getOrCreate(c);
                if (!optData.isPresent())
                    throw new IOException("Invalid item data: " + entry.getKey());
                DataManipulator data = optData.get();
                item.offer(data);
            }
        }

        return item;
    }
}
