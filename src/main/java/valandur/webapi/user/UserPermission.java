package valandur.webapi.user;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import valandur.webapi.misc.TreeNode;

@ConfigSerializable
public class UserPermission {

    @Setting
    private String username;
    public String getUsername() {
        return username;
    }

    @Setting
    private String password;
    public String getPassword() {
        return password;
    }

    @Setting
    private TreeNode<String, Boolean> permissions;
    public TreeNode<String, Boolean> getPermissions() {
        return permissions;
    }


    public UserPermission(String username, String password, TreeNode<String, Boolean> permissions) {
        this.username = username;
        this.password = password;
        this.permissions = permissions;
    }
}
