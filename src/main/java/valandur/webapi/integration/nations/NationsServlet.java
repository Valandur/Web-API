package valandur.webapi.integration.nations;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.object.Nation;
import com.arckenver.nations.object.Rect;
import com.arckenver.nations.object.Zone;
import org.eclipse.jetty.http.HttpMethod;
import valandur.webapi.WebAPI;
import valandur.webapi.api.annotation.WebAPIEndpoint;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.servlet.WebAPIBaseServlet;
import valandur.webapi.json.JsonService;
import valandur.webapi.servlet.base.ServletData;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.UUID;

@WebAPIServlet(basePath = "nations")
public class NationsServlet extends WebAPIBaseServlet {

    public static void onRegister() {
        JsonService json = WebAPI.getJsonService();
        json.registerSerializer(Nation.class, NationSerializer.class);
        json.registerSerializer(Rect.class, RectSerializer.class);
        json.registerSerializer(Zone.class, ZoneSerializer.class);
    }


    @WebAPIEndpoint(method = HttpMethod.GET, path = "nation", perm = "nation.list")
    public void getNations(ServletData data) {
        Collection<Nation> nations = DataHandler.getNations().values();

        data.addJson("ok", true, false);
        data.addJson("nations", nations, data.getQueryParam("details").isPresent());
    }

    @WebAPIEndpoint(method = HttpMethod.GET, path = "nation/:uuid", perm = "nation.one")
    public void getNation(ServletData data, UUID uuid) {
        Nation nation = DataHandler.getNation(uuid);
        if (nation == null) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Nation not found");
            return;
        }

        data.addJson("ok", true, false);
        data.addJson("nation", nation, true);
    }
}
