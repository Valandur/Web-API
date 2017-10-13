package valandur.webapi.servlet;

import org.eclipse.jetty.http.HttpMethod;
import valandur.webapi.WebAPI;
import valandur.webapi.api.servlet.Endpoint;
import valandur.webapi.api.servlet.Servlet;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.servlet.base.ServletData;
import valandur.webapi.servlet.request.auth.AuthRequest;
import valandur.webapi.user.UserPermission;
import valandur.webapi.user.Users;
import valandur.webapi.util.Util;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Servlet(basePath = "user")
public class UserServlet extends BaseServlet {

    @Endpoint(method = HttpMethod.GET, path = "/")
    public void getUserDetails(ServletData data) {
        UserPermission user = data.getUser();
        if (user != null) {
            data.addData("ok", true, false);
            data.addData("user", user, true);
        } else {
            data.addData("ok", false, false);
        }
    }

    @Endpoint(method = HttpMethod.POST, path = "/")
    public void authUser(ServletData data) {
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
        WebAPI.getAuthHandler().addTempPerm(key, perm);

        data.addData("ok", true, false);
        data.addData("key", key, false);
        data.addData("user", perm, false);
    }
}
