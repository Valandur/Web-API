package valandur.webapi.handlers;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthHandler extends AbstractHandler {
    private Map<String, PermissionSet> authMap = new HashMap<>();

    public AuthHandler(List<PermissionSet> permissionSets) {
        for (PermissionSet set : permissionSets) {
            authMap.put(set.getUser(), set);
        }
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String user = request.getHeader("x-webapi-user");
        String token = request.getHeader("x-webapi-token");
        String query = request.getQueryString();

        if (user == null && query != null) {
            String[] splits = query.split("&");
            for (String split : splits) {
                String[] subSplits = split.split("=");
                if (subSplits[0].equalsIgnoreCase("user")) user = subSplits[1];
                if (subSplits[0].equalsIgnoreCase("token")) token = subSplits[1];
            }
        }

        PermissionSet set = authMap.get(user);
        if (set != null && set.getToken().equals(token)) {
            request.setAttribute("perms", set.getPermissions());
        }
    }

    public static class PermissionSet {
        private String user;
        private String token;
        private List<String> permissions;

        public String getUser() {
            return user;
        }
        public String getToken() {
            return token;
        }
        public List<String> getPermissions() {
            return permissions;
        }

        public PermissionSet(String user, String token, List<String> permissions) {
            this.user = user;
            this.token = token;
            this.permissions = permissions;
        }
    }
}
