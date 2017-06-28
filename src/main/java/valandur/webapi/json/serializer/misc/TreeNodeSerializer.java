package valandur.webapi.json.serializer.misc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.api.json.WebAPISerializer;
import valandur.webapi.api.util.TreeNode;

import java.io.IOException;
import java.util.Collection;

public class TreeNodeSerializer extends WebAPISerializer<TreeNode<?, ?>> {
    @Override
    public void serialize(TreeNode<?, ?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value == null) {
            writeValue(provider, false);
            return;
        }

        Boolean val = value.getValue() instanceof Boolean ? (Boolean)value.getValue() : value.getValue() != null;

        Collection<? extends TreeNode<?, ?>> children = value.getChildren();

        if (children.size() == 0) {
            writeValue(provider, val);
            return;
        }

        TreeNode<?, ?> first = children.iterator().next();

        Boolean firstVal = first.getValue() instanceof Boolean ? (Boolean)first.getValue() : first.getValue() != null;
        if (children.size() == 1 && val && first.getKey().toString().equalsIgnoreCase("*") && firstVal) {
            writeValue(provider, "*");
            return;
        }

        gen.writeStartObject();

        if (!val) {
            writeField(provider, ".", false);
        }

        for (TreeNode<?, ?> child : children) {
            String key = child.getKey() instanceof String ? (String)child.getKey() : child.getKey().toString();
            writeField(provider, key, child.getValue());
        }

        gen.writeEndObject();
    }
}
