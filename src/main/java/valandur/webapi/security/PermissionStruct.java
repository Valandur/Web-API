package valandur.webapi.security;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.api.util.TreeNode;

import java.security.Principal;

@ApiModel("Represents a permissions struct that contains information to access the Web-API")
public class PermissionStruct implements Principal {

    private String key = null;
    @ApiModelProperty("The key used authorize with the Web-API")
    public String getKey() {
        return key;
    }

    @JsonIgnore
    @Override
    public String getName() {
        return key;
    }

    private TreeNode<String, Boolean> permissions;
    @ApiModelProperty("The permissions tree that this key grants access to")
    public TreeNode<String, Boolean> getPermissions() {
        return permissions;
    }

    private int rateLimit;
    @ApiModelProperty("The rate limit in requests per second that this key permits (0 = unlimited)")
    public int getRateLimit() {
        return rateLimit;
    }


    public PermissionStruct(TreeNode<String, Boolean> permissions, int rateLimit) {
        this.permissions = permissions;
        this.rateLimit = rateLimit;
    }
    public PermissionStruct(String key, TreeNode<String, Boolean> permissions, int rateLimit) {
        this.key = key;
        this.permissions = permissions;
        this.rateLimit = rateLimit;
    }

    public PermissionStruct withKey(String key) {
        return new PermissionStruct(key, permissions, rateLimit);
    }
}
