package valandur.webapi.permission;

import ninja.leaping.configurate.ConfigurationNode;
import valandur.webapi.misc.TreeNode;

import java.util.*;

public class Permissions {
    /**
     * Returns a permissions tree representing the passed configuration node
     * @param config The configuration node which represents a permission set
     * @return A tree structure representing the config node
     */
    public static TreeNode<String, Boolean> permissionTreeFromConfig(ConfigurationNode config) {
        if (config == null || config.getValue() == null) {
            return new TreeNode<>(false);
        }

        if (!config.hasMapChildren()) {
            if (config.getValue().getClass() == Boolean.class)
                return new TreeNode<>(config.getKey().toString(), config.getBoolean());
            else {
                TreeNode<String, Boolean> node = new TreeNode<>(config.getKey().toString(), true);
                node.addChild(new TreeNode<>("*", true));
                return node;
            }
        }

        TreeNode<String, Boolean> root = new TreeNode<>(config.getKey().toString(), true);
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : config.getChildrenMap().entrySet()) {
            if (entry.getKey().toString().equalsIgnoreCase(".")) {
                root.setValue(entry.getValue().getBoolean());
                continue;
            }

            root.addChild(permissionTreeFromConfig(entry.getValue()));
        }
        return root;
    }
    /**
     * Adds the given permission tree as a config node to the passed base node.
     * @param config The base config node, at which the permissions tree is added
     * @param perms The permissions tree which is parsed to a config node
     */
    public static void permissionTreeToConfig(ConfigurationNode config, TreeNode<String, Boolean> perms) {
        if (perms == null) {
            return;
        }

        Collection<TreeNode<String, Boolean>> children = perms.getChildren();

        if (children.size() == 0) {
            config.setValue(perms.getValue());
            return;
        }

        TreeNode<String, Boolean> first = children.iterator().next();

        if (children.size() == 1 && perms.getValue() && first.getKey().equalsIgnoreCase("*") && first.getValue()) {
            config.setValue("*");
            return;
        }

        if (!perms.getValue()) {
            config.getNode(".").setValue(false);
        }

        for (TreeNode<String, Boolean> child : children) {
            permissionTreeToConfig(config.getNode(child.getKey()), child);
        }
    }

    /**
     * Checks if the specified permissions tree permits the specified permission
     * @param perms The permissions tree
     * @param reqPerms The requested permission to check
     * @return True if the permission tree permits the specified permission, false otherwise
     */
    public static boolean permits(TreeNode<String, Boolean> perms, String[] reqPerms) {
        return permits(perms, Arrays.asList(reqPerms));
    }
    /**
     * Checks if the specified permissions tree permits the specified permission
     * @param perms The permissions tree
     * @param reqPerms The requested permission to check
     * @return True if the permission tree permits the specified permission, false otherwise
     */
    public static boolean permits(TreeNode<String, Boolean> perms, List<String> reqPerms) {
        return  subPermissions(perms, reqPerms).getValue();
    }

    /**
     * Gets the permissions tree representing the allowed actions when following the path along the permissions tree
     * @param perms The permissions tree
     * @param path The permission path to follow in the permissions tree
     * @return The sub tree representing the permission permitted for that path
     */
    public static TreeNode<String, Boolean> subPermissions(TreeNode<String, Boolean> perms, String[] path) {
        return subPermissions(perms, Arrays.asList(path));
    }
    /**
     * Gets the permissions tree representing the allowed actions when following the path along the permissions tree
     * @param perms The permissions tree
     * @param path The permission path to follow in the permissions tree
     * @return The sub tree representing the permission permitted for that path
     */
    public static TreeNode<String, Boolean> subPermissions(TreeNode<String, Boolean> perms, List<String> path) {
        // Check if we ourselves already are a permit-all permission
        if (perms.getKey() != null && perms.getKey().equalsIgnoreCase("*") && perms.getValue()) {
            return perms;
        }

        // Go through all levels of the required permission
        for (String perm : path) {
            // Get the specific permission node for this level, if we have one
            Optional<TreeNode<String, Boolean>> subPerm = perms.getChild(perm);
            if (subPerm.isPresent()) {
                perms = subPerm.get();
                continue;
            }

            // If we don't have a specific permission for this level, check if there is a "*" permission
            Optional<TreeNode<String, Boolean>> allChild = perms.getChild("*");
            return allChild.orElseGet(Permissions::emptyNode);
        }

        // If we get here then that means we have an exact permission for this path
        // or the path had no elements
        return perms;
    }

    /**
     * Gets an empty permissions node
     * @return An empty permissions node
     */
    public static TreeNode<String, Boolean> emptyNode() {
        return new TreeNode<>(false);
    }
    /**
     * Constructs a node which permits all operations
     * @return A node that allowed all operations
     */
    public static TreeNode<String, Boolean> permitAllNode() {
        TreeNode<String, Boolean> node = new TreeNode<>(true);
        node.addChild(new TreeNode<>("*", true));
        return node;
    }
}
