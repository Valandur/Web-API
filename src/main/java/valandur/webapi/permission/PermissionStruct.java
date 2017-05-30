package valandur.webapi.permission;

import valandur.webapi.misc.TreeNode;

public class PermissionStruct {
    private TreeNode<String, Boolean> permissions;
    public TreeNode<String, Boolean> getPermissions() {
        return permissions;
    }

    private int rateLimit;
    public int getRateLimit() {
        return rateLimit;
    }

    public PermissionStruct(TreeNode<String, Boolean> permissions, int rateLimit) {
        this.permissions = permissions;
        this.rateLimit = rateLimit;
    }
}
