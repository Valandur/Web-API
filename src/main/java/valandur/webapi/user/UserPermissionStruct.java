package valandur.webapi.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.api.util.TreeNode;
import valandur.webapi.security.PermissionStruct;

@ApiModel("UserPermissionStruct")
public class UserPermissionStruct extends PermissionStruct {

    private static final int MAX_REQUESTS_PER_SECOND = 0;

    private String username;
    @ApiModelProperty("The username of this user")
    public String getUsername() {
        return username;
    }
    @JsonIgnore
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


    public UserPermissionStruct(String username, String password, TreeNode<String, Boolean> permissions) {
        super(permissions, MAX_REQUESTS_PER_SECOND);

        this.username = username;
        this.password = password;
    }

    @JsonIgnore
    public void setPassword(String password) {
        this.password = password;
    }
}
