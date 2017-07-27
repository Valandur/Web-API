package valandur.webapi.json.serializer.misc;

import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.api.util.TreeNode;

import java.io.IOException;
import java.util.Collection;

public class TreeNodeSerializer extends WebAPIBaseSerializer<TreeNode<?, ?>> {
    @Override
    public void serialize(TreeNode<?, ?> value) throws IOException {
        if (value == null) {
            writeValue(false);
            return;
        }

        Boolean val = value.getValue() instanceof Boolean ? (Boolean)value.getValue() : value.getValue() != null;

        Collection<? extends TreeNode<?, ?>> children = value.getChildren();

        if (children.size() == 0) {
            writeValue(val);
            return;
        }

        TreeNode<?, ?> first = children.iterator().next();

        Boolean firstVal = first.getValue() instanceof Boolean ? (Boolean)first.getValue() : first.getValue() != null;
        if (children.size() == 1 && val && first.getKey().toString().equalsIgnoreCase("*") && firstVal) {
            writeValue("*");
            return;
        }

        writeStartObject();

        if (!val) {
            writeField(".", false);
        }

        for (TreeNode<?, ?> child : children) {
            String key = child.getKey() instanceof String ? (String)child.getKey() : child.getKey().toString();
            writeField(key, child.getValue());
        }

        writeEndObject();
    }
}
