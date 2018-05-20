package valandur.webapi.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import valandur.webapi.security.PermissionStruct;
import valandur.webapi.util.TreeNode;

@ApiModel("UserPermissionStruct")
public class UserPermissionStruct extends PermissionStruct {

    private static final int MAX_REQUESTS_PER_SECOND = 0;

    private String username;

    @Override
    public String getName() {
        return username;
    }

    @JsonIgnore
    private String password;
    @JsonIgnore
    public String getPassword() {
        return password;
    }


    public UserPermissionStruct(String username, String password, TreeNode permissions) {
        super(permissions, MAX_REQUESTS_PER_SECOND);

        this.username = username;
        this.password = password;
    }

    @JsonIgnore
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public PermissionStruct withKey(String key) {
        this.key = key;
        return this;
    }
    public UserPermissionStruct withPermissions(TreeNode permissions) {
        return new UserPermissionStruct(username, password, permissions);
    }
}
