package valandur.webapi.security;

import valandur.webapi.api.util.TreeNode;
import valandur.webapi.user.UserPermissionStruct;

import java.security.Principal;

public class SecurityContext implements javax.ws.rs.core.SecurityContext {

    private PermissionStruct perms;
    public PermissionStruct getPermissionStruct() {
        return perms;
    }

    private TreeNode<String, Boolean> endpointPerms;
    public TreeNode<String, Boolean> getEndpointPerms() {
        return endpointPerms;
    }
    public void setEndpointPerms(TreeNode<String, Boolean> endpointPerms) {
        this.endpointPerms = endpointPerms;
    }

    @Override
    public Principal getUserPrincipal() {
        return perms;
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public boolean isSecure() {
        return perms instanceof UserPermissionStruct;
    }

    @Override
    public String getAuthenticationScheme() {
        return "WEBAPI-PERMS";
    }


    public SecurityContext(PermissionStruct perms) {
        this.perms = perms;
    }
}
