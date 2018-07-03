package valandur.webapi.security;

import valandur.webapi.WebAPI;
import valandur.webapi.user.UserPermissionStruct;
import valandur.webapi.util.TreeNode;

import java.security.Principal;

public class SecurityContext implements javax.ws.rs.core.SecurityContext {

    private PermissionStruct perms;
    public PermissionStruct getPermissionStruct() {
        return perms;
    }

    private TreeNode endpointPerms;
    public TreeNode getEndpointPerms() {
        return endpointPerms;
    }
    public void setEndpointPerms(TreeNode endpointPerms) {
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

    public boolean hasPerms(String... reqPerms) {
        return WebAPI.getSecurityService().subPermissions(endpointPerms, reqPerms).getValue();
    }


    public SecurityContext(PermissionStruct perms) {
        this.perms = perms;
    }
}
