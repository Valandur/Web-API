package valandur.webapi.servlet.user;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.mindrot.jbcrypt.BCrypt;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.WebAPI;
import valandur.webapi.permission.Permission;
import valandur.webapi.permission.PermissionStruct;
import valandur.webapi.servlet.ServletData;
import valandur.webapi.servlet.WebAPIServlet;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class UserServlet extends WebAPIServlet {

    private static final String configFileName = "user.conf";

    private WebAPI api;
    private ConfigurationLoader loader;
    private ConfigurationNode config;

    private List<UserPermission> perms = new ArrayList<>();


    public UserServlet() {
        Tuple<ConfigurationLoader, ConfigurationNode> tup = WebAPI.getInstance()
                .loadWithDefaults(configFileName, "defaults/" + configFileName);
        loader = tup.getFirst();
        config = tup.getSecond();

        try {
            perms = config.getNode("users").getList(TypeToken.of(UserPermission.class));
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void handlePost(ServletData data) {
        Optional<AuthRequest> optReq = data.getRequestBody(AuthRequest.class);
        if (!optReq.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid auth data: " + data.getLastParseError().getMessage());
            return;
        }

        AuthRequest req = optReq.get();

        Optional<UserPermission> optPerm = perms.stream().filter(p -> p.getUsername().equals(req.getUsername()) &&
                BCrypt.checkpw(req.getPassword(), p.getPassword())).findFirst();

        if (!optPerm.isPresent()) {
            data.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid username / password");
            return;
        }

        UserPermission perm = optPerm.get();

        String key = UUID.randomUUID().toString();
        WebAPI.getInstance().getAuthHandler().addTempPerm(key, new PermissionStruct(perm.getPermissions(), 0));

        data.addJson("ok", true, false);
        data.addJson("key", key, false);
    }
}
