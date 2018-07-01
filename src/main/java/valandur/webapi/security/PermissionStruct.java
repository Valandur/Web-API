package valandur.webapi.security;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import valandur.webapi.util.TreeNode;

import java.security.Principal;

@ApiModel(value = "PermissionStruct", description = "Represents a permissions struct that contains information to access the Web-API")
public class PermissionStruct implements Principal {

    protected String key = null;
    @ApiModelProperty(value = "The key used authorize with the Web-API", required = true)
    public String getKey() {
        return key;
    }

    @Override
    @ApiModelProperty(value = "The human readable name of this permssions struct. Only useful for users.", required = true)
    public String getName() {
        return key;
    }

    private TreeNode permissions;
    @ApiModelProperty(dataType = "object", value = "The permissions tree that this key grants access to", required = true)
    public TreeNode getPermissions() {
        return permissions;
    }

    private int rateLimit;
    @ApiModelProperty(value = "The rate limit in requests per second that this key permits (0 = unlimited)", required = true)
    public int getRateLimit() {
        return rateLimit;
    }


    public PermissionStruct(TreeNode permissions, int rateLimit) {
        this.permissions = permissions;
        this.rateLimit = rateLimit;
    }
    public PermissionStruct(String key, TreeNode permissions, int rateLimit) {
        this.key = key;
        this.permissions = permissions;
        this.rateLimit = rateLimit;
    }

    public PermissionStruct withKey(String key) {
        return new PermissionStruct(key, permissions, rateLimit);
    }
}
