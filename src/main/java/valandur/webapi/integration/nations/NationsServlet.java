package valandur.webapi.integration.nations;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.object.Nation;
import com.arckenver.nations.object.Rect;
import com.arckenver.nations.object.Zone;
import org.eclipse.jetty.http.HttpMethod;
import valandur.webapi.api.WebAPIAPI;
import valandur.webapi.api.json.IJsonService;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Endpoint;
import valandur.webapi.api.servlet.IServletData;
import valandur.webapi.api.servlet.Servlet;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.UUID;

@Servlet(basePath = "nations")
public class NationsServlet extends BaseServlet {

    public static void onRegister() {
        IJsonService json = WebAPIAPI.getJsonService().get();
        json.registerCache(Nation.class, CachedNation.class);
        json.registerCache(Rect.class, CachedRect.class);
        json.registerCache(Zone.class, CachedZone.class);
    }


    @Endpoint(method = HttpMethod.GET, path = "nation", perm = "nation.list")
    public void getNations(IServletData data) {
        Collection<Nation> nations = DataHandler.getNations().values();

        data.addJson("ok", true, false);
        data.addJson("nations", nations, data.getQueryParam("details").isPresent());
    }

    @Endpoint(method = HttpMethod.GET, path = "nation/:uuid", perm = "nation.one")
    public void getNation(IServletData data, UUID uuid) {
        Nation nation = DataHandler.getNation(uuid);
        if (nation == null) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Nation not found");
            return;
        }

        data.addJson("ok", true, false);
        data.addJson("nation", nation, true);
    }
}
