package valandur.webapi.util;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a tree structure where each node as a key and a value.
 */
public class TreeNode {

    private String key;
    private boolean value;

    private TreeNode parent;
    private Map<String, TreeNode> children = new HashMap<>();


    /**
     * Gets the key of the current node.
     * @return The key of the current node.
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the value of the current node.
     * @return The value of the current node.
     */
    public boolean getValue() {
        return value;
    }


    @JsonValue
    public Object anyGetter() {
        if (this.children.size() == 0) {
            return this.value;
        } else {
            Map<String, Object> map = new HashMap<>();
            if (!this.value) {
                map.put(".", false);
            }
            for (Map.Entry<String, TreeNode> entry : children.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
            return map;
        }
    }
    @JsonAnySetter
    public void anySetter(String key, Object value) {
        if (".".equalsIgnoreCase(key)) {
            this.value = (boolean)value;
            return;
        }

        if (value instanceof Map) {
            this.addChild(new TreeNode(key, (Map<String, Object>)value));
        } else {
            this.addChild(new TreeNode(key, (boolean)value));
        }
    }

    /**
     * Gets the parent node of this node, or null if this node has no parent.
     * @return The parent node of this node. Null if this node doesn't have a parent.
     */
    @JsonIgnore
    public TreeNode getParent() {
        return parent;
    }

    /**
     * Gets the direct child of this node with the specified key
     * @param key The key which identifies the child
     * @return An optional containing the child with the specified key if it was found.
     */
    public Optional<TreeNode> getChild(String key) {
        TreeNode child = children.get(key);
        return child != null ? Optional.of(child) : Optional.empty();
    }

    /**
     * Gets all direct children of the current node.
     * @return A collection of all the children of this node.
     */
    public Collection<TreeNode> getChildren() {
        return children.values();
    }

    /**
     * Creates a new (root) node with null as the key and 'true' as the value.
     */
    public TreeNode() {
        this.value = true;
    }

    /**
     * Creates a new (root) node with null as the key and the specified value.
     * @param value The value of this node.
     */
    public TreeNode(boolean value) {
        this.value = value;
    }

    /**
     * Creates a new node with the specified key and value.
     * @param key The key of the new node.
     * @param value The value of the new node.
     */
    public TreeNode(String key, boolean value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Creates a new node with the specified key and value. Also adds the node to the children of the specified parent.
     * @param key The key of the new node.
     * @param value The value of the new node.
     * @param parent The parent node which this new node is attached to.
     */
    public TreeNode(String key, boolean value, TreeNode parent) {
        this(key, value);
        setParent(parent);
    }

    /**
     * Creates a new node with the specified key and children.
     * @param key The key of the new node.
     * @param children A map of child nodes of this node.
     */
    public TreeNode(String key, Map<String, Object> children) {
        this.key = key;
        this.value = true;

        for (Map.Entry<String, Object> entry : children.entrySet()) {
            if (".".equalsIgnoreCase(entry.getKey())) {
                this.value = (boolean)entry.getValue();
                continue;
            }

            if (entry.getValue() instanceof Map) {
                this.addChild(new TreeNode(entry.getKey(), (Map<String, Object>)entry.getValue()));
            } else {
                this.addChild(new TreeNode(entry.getKey(), (boolean)entry.getValue()));
            }
        }
    }

    /**
     * Sets the value of this node.
     * @param value The new value of this node.
     */
    public void setValue(boolean value) {
        this.value = value;
    }

    /**
     * Sets the parent of this node. This registers this node in the list of children for the specified parent.
     * It also removes this node from any previous parent.
     * @param parent
     */
    public void setParent(TreeNode parent) {
        if (this.parent != null) {
            this.parent.doRemoveChild(this);
        }

        parent.doAddChild(this);
    }

    /**
     * Adds a new child node to this node.
     * @param child The child to attach to this node.
     * @return The child node.
     */
    public TreeNode addChild(TreeNode child) {
        return doAddChild(child);
    }
    private TreeNode doAddChild(TreeNode child) {
        child.parent = this;
        children.put(child.key, child);
        return child;
    }

    /**
     * Removes the child with the specified key from the list of children.
     * @param key The key which identifies the child.
     * @return The child node.
     */
    public TreeNode removeChild(String key) {
        return doRemoveChild(children.get(key));
    }

    /**
     * Removes the specified child from the list of children of this node.
     * @param child The child to remove.
     * @return The child node.
     */
    public TreeNode removeChild(TreeNode child) {
        return doRemoveChild(child);
    }
    private TreeNode doRemoveChild(TreeNode child) {
        child.parent = null;
        return children.remove(child.key);
    }

    @Override
    public String toString() {
        return "[" + (key != null ? key : "") + ":" + value + "]";
    }
}
