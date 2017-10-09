package valandur.webapi.integration.nations;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.object.Nation;
import com.arckenver.nations.object.Rect;
import com.arckenver.nations.object.Zone;
import org.eclipse.jetty.http.HttpMethod;
import valandur.webapi.api.WebAPIAPI;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Endpoint;
import valandur.webapi.api.servlet.IServletData;
import valandur.webapi.api.servlet.Servlet;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Servlet(basePath = "nations")
public class NationsServlet extends BaseServlet {

    public static void onRegister() {
        WebAPIAPI.getJsonService().ifPresent(srv -> {
            srv.registerCache(Nation.class, CachedNation.class);
            srv.registerCache(Rect.class, CachedRect.class);
            srv.registerCache(Zone.class, CachedZone.class);
        });
    }


    @Endpoint(method = HttpMethod.GET, path = "nation", perm = "nation.list")
    public void getNations(IServletData data) {
        Optional<Collection<CachedNation>> optNations = WebAPIAPI.runOnMain(
                () -> DataHandler.getNations().values().stream().map(CachedNation::new).collect(Collectors.toList())
        );

        data.addJson("ok", optNations.isPresent(), false);
        data.addJson("nations", optNations.orElse(null), data.getQueryParam("details").isPresent());
    }

    @Endpoint(method = HttpMethod.GET, path = "nation/:uuid", perm = "nation.one")
    public void getNation(IServletData data, UUID uuid) {
        Optional<CachedNation> optNation = WebAPIAPI.runOnMain(() -> new CachedNation(DataHandler.getNation(uuid)));

        data.addJson("ok", optNation.isPresent(), false);
        data.addJson("nation", optNation.orElse(null), true);
    }
}
