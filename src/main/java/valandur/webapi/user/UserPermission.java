package valandur.webapi.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import valandur.webapi.api.util.TreeNode;
import valandur.webapi.permission.PermissionStruct;

public class UserPermission {

    private String username;
    public String getUsername() {
        return username;
    }

    private String password;
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    private TreeNode<String, Boolean> permissions;
    public TreeNode<String, Boolean> getPermissions() {
        return permissions;
    }

    private PermissionStruct pStruct;
    @JsonIgnore
    public PermissionStruct getPermissionStruct() {
        return pStruct;
    }


    public UserPermission(String username, String password, TreeNode<String, Boolean> permissions) {
        this.username = username;
        this.password = password;
        this.permissions = permissions;
        this.pStruct = new PermissionStruct(this.permissions, 0);
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
