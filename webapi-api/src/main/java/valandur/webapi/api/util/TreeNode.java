package valandur.webapi.api.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a tree structure where each node as a key and a value.
 * @param <K> The type of the keys
 * @param <V> The type of the values
 */
public class TreeNode<K, V> {

    private K key;
    private V value;

    private TreeNode<K, V> parent;
    private Map<K, TreeNode<K, V>> children = new HashMap<>();


    /**
     * Gets the key of the current node.
     * @return The key of the current node.
     */
    public K getKey() {
        return key;
    }

    /**
     * Gets the value of the current node.
     * @return The value of the current node.
     */
    public V getValue() {
        return value;
    }


    @JsonValue
    public Object anyGetter() {
        if (this.children.size() == 0) {
            return this.value;
        } else {
            Map<String, Object> map = new HashMap<>();
            for (Map.Entry<K, TreeNode<K, V>> entry : children.entrySet()) {
                map.put(entry.getKey().toString(), entry.getValue());
            }
            return map;
        }
    }

    /**
     * Gets the parent node of this node, or null if this node has no parent.
     * @return The parent node of this node. Null if this node doesn't have a parent.
     */
    @JsonIgnore
    public TreeNode<K, V> getParent() {
        return parent;
    }

    /**
     * Gets the direct child of this node with the specified key
     * @param key The key which identifies the child
     * @return An optional containing the child with the specified key if it was found.
     */
    public Optional<TreeNode<K, V>> getChild(K key) {
        TreeNode<K, V> child = children.get(key);
        return child != null ? Optional.of(child) : Optional.empty();
    }

    /**
     * Gets the exact child of this node with the specified key. This traverses children recursivly until the keys
     * array is exhausted.
     * @param keys The array of keys that are traversed in search of the child.
     * @return An optional containing the child with the specified key if it was found.
     */
    public Optional<TreeNode<K, V>> getChild(K[] keys) {
        TreeNode<K, V> curr = this;
        for (K key : keys) {
            Optional<TreeNode<K, V>> subCurr = curr.getChild(key);
            if (!subCurr.isPresent())
                return Optional.empty();
            curr = subCurr.get();
        }
        return Optional.of(curr);
    }

    /**
     * Gets all direct children of the current node.
     * @return A collection of all the children of this node.
     */
    public Collection<TreeNode<K, V>> getChildren() {
        return children.values();
    }


    /**
     * Creates a new (root) node with null as the key and the specified value.
     * @param value The value of this node.
     */
    public TreeNode(V value) {
        this.value = value;
    }

    /**
     * Creates a new node with the specified key and value.
     * @param key The key of the new node.
     * @param value The value of the new node.
     */
    public TreeNode(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Creates a new node with the specified key and value. Also adds the node to the children of the specified parent.
     * @param key The key of the new node.
     * @param value The value of the new node.
     * @param parent The parent node which this new node is attached to.
     */
    public TreeNode(K key, V value, TreeNode<K, V> parent) {
        this(key, value);
        setParent(parent);
    }

    /**
     * Sets the value of this node.
     * @param value The new value of this node.
     */
    public void setValue(V value) {
        this.value = value;
    }

    /**
     * Sets the parent of this node. This registers this node in the list of children for the specified parent.
     * It also removes this node from any previous parent.
     * @param parent
     */
    public void setParent(TreeNode<K, V> parent) {
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
    public TreeNode<K, V> addChild(TreeNode<K, V> child) {
        return doAddChild(child);
    }
    private TreeNode<K, V> doAddChild(TreeNode<K, V> child) {
        child.parent = this;
        children.put(child.key, child);
        return child;
    }

    /**
     * Removes the child with the specified key from the list of children.
     * @param key The key which identifies the child.
     * @return The child node.
     */
    public TreeNode<K, V> removeChild(K key) {
        return doRemoveChild(children.get(key));
    }

    /**
     * Removes the specified child from the list of children of this node.
     * @param child The child to remove.
     * @return The child node.
     */
    public TreeNode<K, V> removeChild(TreeNode<K, V> child) {
        return doRemoveChild(child);
    }
    private TreeNode<K, V> doRemoveChild(TreeNode<K, V> child) {
        child.parent = null;
        return children.remove(child.key);
    }

    @Override
    public String toString() {
        return "[" + (key != null ? key.toString() : "") + ":" + (value != null ? value.toString() : null) + "]";
    }
}
