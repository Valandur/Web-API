package valandur.webapi.api.permission;

import ninja.leaping.configurate.ConfigurationNode;
import valandur.webapi.api.util.TreeNode;

import java.util.List;

/**
 * The permission service handles access permissions to routes within the Web-API.
 */
public interface IPermissionService {

    /**
     * Returns a permissions tree representing the passed configuration node
     * @param config The configuration node which represents a permission set
     * @return A tree structure representing the config node
     */
    TreeNode permissionTreeFromConfig(ConfigurationNode config);
    /**
     * Adds the given permission tree as a config node to the passed base node.
     * @param config The base config node, at which the permissions tree is added
     * @param perms The permissions tree which is parsed to a config node
     */
    void permissionTreeToConfig(ConfigurationNode config, TreeNode perms);

    /**
     * Checks if the specified permissions tree permits the specified permission
     * @param perms The permissions tree
     * @param reqPerms The requested permission to check
     * @return True if the permission tree permits the specified permission, false otherwise
     */
    boolean permits(TreeNode perms, String[] reqPerms);
    /**
     * Checks if the specified permissions tree permits the specified permission
     * @param perms The permissions tree
     * @param reqPerms The requested permission to check
     * @return True if the permission tree permits the specified permission, false otherwise
     */
    boolean permits(TreeNode perms, List<String> reqPerms);

    /**
     * Gets the permissions tree representing the allowed actions when following the path along the permissions tree
     * @param perms The permissions tree
     * @param path The permission path to follow in the permissions tree
     * @return The sub tree representing the permission permitted for that path
     */
    TreeNode subPermissions(TreeNode perms, String[] path);
    /**
     * Gets the permissions tree representing the allowed actions when following the path along the permissions tree
     * @param perms The permissions tree
     * @param path The permission path to follow in the permissions tree
     * @return The sub tree representing the permission permitted for that path
     */
    TreeNode subPermissions(TreeNode perms, List<String> path);

    /**
     * Returns an empty node, i.e. a node that has no permissions.
     * @return An empty permissions node
     */
    static TreeNode emptyNode() {
        return new TreeNode(false);
    }

    /**
     * Returns a grant-all permissions node, i.e. a node that contains all permissions.
     * @return A grant-all permissions node
     */
    static TreeNode permitAllNode() {
        TreeNode node = new TreeNode(true);
        node.addChild(new TreeNode("*", true));
        return node;
    }
}
