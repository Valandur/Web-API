package valandur.webapi.servlet.user;

import valandur.webapi.WebAPI;
import valandur.webapi.misc.Util;
import valandur.webapi.permission.PermissionStruct;
import valandur.webapi.servlet.ServletData;
import valandur.webapi.servlet.WebAPIServlet;
import valandur.webapi.user.UserPermission;
import valandur.webapi.user.Users;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class UserServlet extends WebAPIServlet {
    @Override
    protected void handlePost(ServletData data) {
        Optional<AuthRequest> optReq = data.getRequestBody(AuthRequest.class);
        if (!optReq.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid auth data: " + data.getLastParseError().getMessage());
            return;
        }

        AuthRequest req = optReq.get();

        Optional<UserPermission> optPerm = Users.getUser(req.getUsername(), req.getPassword());
        if (!optPerm.isPresent()) {
            data.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid username / password");
            return;
        }

        UserPermission perm = optPerm.get();

        String key = Util.generateUniqueId();
        WebAPI.getInstance().getAuthHandler().addTempPerm(key, new PermissionStruct(perm.getPermissions(), 0));

        data.addJson("ok", true, false);
        data.addJson("key", key, false);
    }
}
