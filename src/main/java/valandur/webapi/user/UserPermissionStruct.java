package valandur.webapi.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import valandur.webapi.api.util.TreeNode;
import valandur.webapi.security.PermissionStruct;

public class UserPermissionStruct extends PermissionStruct {

    private static final int MAX_REQUESTS_PER_SECOND = 0;

    private String username;
    public String getUsername() {
        return username;
    }
    @JsonIgnore
    @Override
    public String getName() {
        return username;
    }

    private String password;
    @JsonIgnore
    public String getPassword() {
        return password;
    }


    public UserPermissionStruct(String username, String password, TreeNode<String, Boolean> permissions) {
        super(permissions, MAX_REQUESTS_PER_SECOND);

        this.username = username;
        this.password = password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
