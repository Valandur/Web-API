package valandur.webapi.security;

import ninja.leaping.configurate.ConfigurationNode;
import valandur.webapi.util.TreeNode;

import java.util.*;

/**
 * The permission service handles access permissions to routes within the Web-API.
 */
public class PermissionService {

    /**
     * Returns a permissions tree representing the passed configuration node
     * @param config The configuration node which represents a permission set
     * @return A tree structure representing the config node
     */
    public TreeNode permissionTreeFromConfig(ConfigurationNode config) {
        if (config == null || config.getValue() == null) {
            return new TreeNode(false);
        }

        if (!config.hasMapChildren()) {
            if (config.getValue().getClass() == Boolean.class)
                return new TreeNode(config.getKey().toString(), config.getBoolean());
            else {
                TreeNode node = new TreeNode(config.getKey().toString(), true);
                node.addChild(new TreeNode("*", true));
                return node;
            }
        }

        TreeNode root = new TreeNode(config.getKey().toString(), true);
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
    public void permissionTreeToConfig(ConfigurationNode config, TreeNode perms) {
        if (perms == null) {
            return;
        }

        Collection<TreeNode> children = perms.getChildren();

        if (children.size() == 0) {
            config.setValue(perms.getValue());
            return;
        }

        if (!perms.getValue()) {
            config.getNode(".").setValue(false);
        }

        for (TreeNode child : children) {
            permissionTreeToConfig(config.getNode(child.getKey()), child);
        }
    }

    /**
     * Checks if the specified permissions tree permits the specified permission
     * @param perms The permissions tree
     * @param reqPerms The requested permission to check
     * @return True if the permission tree permits the specified permission, false otherwise
     */
    public boolean permits(TreeNode perms, String[] reqPerms) {
        return permits(perms, Arrays.asList(reqPerms));
    }

    /**
     * Checks if the specified permissions tree permits the specified permission
     * @param perms The permissions tree
     * @param reqPerms The requested permission to check
     * @return True if the permission tree permits the specified permission, false otherwise
     */
    public boolean permits(TreeNode perms, List<String> reqPerms) {
        return  subPermissions(perms, reqPerms).getValue();
    }

    /**
     * Gets the permissions tree representing the allowed actions when following the path along the permissions tree
     * @param perms The permissions tree
     * @param path The permission path to follow in the permissions tree
     * @return The sub tree representing the permission permitted for that path
     */
    public TreeNode subPermissions(TreeNode perms, String[] path) {
        return subPermissions(perms, Arrays.asList(path));
    }

    /**
     * Gets the permissions tree representing the allowed actions when following the path along the permissions tree
     * @param perms The permissions tree
     * @param path The permission path to follow in the permissions tree
     * @return The sub tree representing the permission permitted for that path
     */
    public TreeNode subPermissions(TreeNode perms, List<String> path) {
        // If we don't have any permissions return an empty node
        if (perms == null) {
            return PermissionService.emptyNode();
        }

        // Check if we ourselves already are a permit-all permission
        if (perms.getKey() != null && perms.getKey().equalsIgnoreCase("*") && perms.getValue()) {
            return perms;
        }

        // Go through all levels of the required permission
        for (String perm : path) {
            // Get the specific permission node for this level, if we have one
            Optional<TreeNode> subPerm = perms.getChild(perm);
            if (subPerm.isPresent()) {
                perms = subPerm.get();
                continue;
            }

            // If we don't have a specific permission for this level, check if there is a "*" permission
            Optional<TreeNode> allChild = perms.getChild("*");
            return allChild.orElseGet(PermissionService::emptyNode);
        }

        // If we get here then that means we have an exact permission for this path
        // or the path had no elements
        return perms;
    }

    /**
     * Returns an empty node, i.e. a node that has no permissions.
     * @return An empty permissions node
     */
    public static TreeNode emptyNode() {
        return new TreeNode(false);
    }

    /**
     * Returns a grant-all permissions node, i.e. a node that contains all permissions.
     * @return A grant-all permissions node
     */
    public static TreeNode permitAllNode() {
        TreeNode node = new TreeNode(true);
        node.addChild(new TreeNode("*", true));
        return node;
    }
}
