package valandur.webapi.misc;

import ninja.leaping.configurate.ConfigurationNode;

import java.util.*;

public class TreeNode<K, V> {
    private K key;
    public K getKey() {
        return key;
    }

    private V value;
    public V getValue() {
        return value;
    }

    private TreeNode<K, V> parent;
    public TreeNode<K, V> getParent() {
        return parent;
    }

    private Map<K, TreeNode<K, V>> children = new HashMap<>();
    public Optional<TreeNode<K, V>> getChild(K key) {
        TreeNode<K, V> child = children.get(key);
        return child != null ? Optional.of(child) : Optional.empty();
    }
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
    public Collection<TreeNode<K, V>> getChildren() {
        return children.values();
    }


    public TreeNode(V value) {
        this.value = value;
    }
    public TreeNode(K key, V value) {
        this.key = key;
        this.value = value;
    }
    public TreeNode(K key, V value, TreeNode<K, V> parent) {
        this(key, value);
        setParent(parent);
    }

    public void setValue(V value) {
        this.value = value;
    }

    public void setParent(TreeNode<K, V> parent) {
        if (this.parent != null) {
            this.parent.doRemoveChild(this);
        }

        parent.doAddChild(this);
    }

    public TreeNode<K, V> addChild(TreeNode<K, V> child) {
        return doAddChild(child);
    }
    private TreeNode<K, V> doAddChild(TreeNode<K, V> child) {
        child.parent = this;
        children.put(child.key, child);
        return child;
    }

    public TreeNode<K, V> removeChild(K key) {
        return doRemoveChild(children.get(key));
    }
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
