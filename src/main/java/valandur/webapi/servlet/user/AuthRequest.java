package valandur.webapi.servlet.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize
public class AuthRequest {

    @JsonDeserialize
    private String username;
    public String getUsername() {
        return username;
    }

    @JsonDeserialize
    private String password;
    public String getPassword() {
        return password;
    }
}
