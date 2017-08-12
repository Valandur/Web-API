package valandur.webapi.integration.nucleus;

import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import io.github.nucleuspowered.nucleus.api.nucleusdata.Home;
import io.github.nucleuspowered.nucleus.api.nucleusdata.NamedLocation;
import io.github.nucleuspowered.nucleus.api.service.NucleusHomeService;
import io.github.nucleuspowered.nucleus.api.service.NucleusJailService;
import io.github.nucleuspowered.nucleus.api.service.NucleusKitService;
import org.eclipse.jetty.http.HttpMethod;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;
import valandur.webapi.api.annotation.WebAPIEndpoint;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.servlet.WebAPIBaseServlet;
import valandur.webapi.json.JsonService;
import valandur.webapi.servlet.ServletData;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@WebAPIServlet(basePath = "nucleus")
public class NucleusServlet extends WebAPIBaseServlet {

    public static void onRegister() {
        JsonService json = WebAPI.getJsonService();
        json.registerSerializer(NamedLocation.class, NamedLocationSerializer.class);
        json.registerSerializer(CachedKit.class, CachedKitSerializer.class);
        json.registerSerializer(Home.class, HomeSerializer.class);
    }


    @WebAPIEndpoint(method = HttpMethod.GET, path = "jail", perm = "jail.list")
    public void getJails(ServletData data) {
        Optional<NucleusJailService> optSrv = NucleusAPI.getJailService();
        if (!optSrv.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Nuclues jail service not available");
            return;
        }

        NucleusJailService srv = optSrv.get();

        data.addJson("ok", true, false);
        data.addJson("jails", srv.getJails().values(), data.getQueryParam("details").isPresent());
    }

    @WebAPIEndpoint(method = HttpMethod.GET, path = "jail/:name", perm = "jail.one")
    public void getJail(ServletData data, String name) {
        Optional<NucleusJailService> optSrv = NucleusAPI.getJailService();
        if (!optSrv.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Nuclues jail service not available");
            return;
        }

        NucleusJailService srv = optSrv.get();

        Optional<NamedLocation> optJail = srv.getJail(name);
        if (!optJail.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Jail not found");
            return;
        }

        data.addJson("ok", true, false);
        data.addJson("jail", optJail.get(), true);
    }

    @WebAPIEndpoint(method = HttpMethod.POST, path = "jail", perm = "jail.create")
    public void addJail(ServletData data) {
        Optional<NucleusJailService> optSrv = NucleusAPI.getJailService();
        if (!optSrv.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Nuclues jail service not available");
            return;
        }

        NucleusJailService srv = optSrv.get();

        Optional<CreateJailRequest> optReq = data.getRequestBody(CreateJailRequest.class);
        if (!optReq.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid jail data: " + data.getLastParseError().getMessage());
            return;
        }

        CreateJailRequest req = optReq.get();

        if (!req.getWorld().isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "No valid world provided");
            return;
        }

        if (req.getPosition() == null) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "No valid position provided");
            return;
        }

        Optional<NamedLocation> resJail = WebAPI.runOnMain(() -> {
            Optional<?> optWorld = req.getWorld().get().getLive();
            if (!optWorld.isPresent())
                return null;

            World w = (World)optWorld.get();
            srv.setJail(req.getName(), new Location<>(w, req.getPosition()), req.getRotation());
            return srv.getJail(req.getName()).orElse(null);
        });

        if (!resJail.isPresent()) {
            data.addJson("ok", false, false);
            return;
        }

        data.setStatus(HttpServletResponse.SC_CREATED);
        data.addJson("ok", true, false);
        data.addJson("jail", resJail.get(), data.getQueryParam("details").isPresent());
    }

    @WebAPIEndpoint(method = HttpMethod.PUT, path = "jail/:name", perm = "jail.change")
    public void changeJail(ServletData data) {
        data.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Not implemented");
    }

    @WebAPIEndpoint(method = HttpMethod.DELETE, path = "jail/:name", perm = "jail.delete")
    public void removeJail(ServletData data, String name) {
        Optional<NucleusJailService> optSrv = NucleusAPI.getJailService();
        if (!optSrv.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Nuclues jail service not available");
            return;
        }

        NucleusJailService srv = optSrv.get();

        Optional<NamedLocation> optJail = srv.getJail(name);
        if (!optJail.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Jail not found");
            return;
        }

        srv.removeJail(name);

        data.addJson("ok", true, false);
        data.addJson("jail", optJail.get(), true);
    }


    @WebAPIEndpoint(method = HttpMethod.GET, path = "kit", perm = "kit.list")
    public void getKits(ServletData data) {
        Optional<NucleusKitService> optSrv = NucleusAPI.getKitService();
        if (!optSrv.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Nuclues kit service not available");
            return;
        }

        NucleusKitService srv = optSrv.get();
        List<CachedKit> kits = new ArrayList<>();
        for (String name : srv.getKitNames()) {
            srv.getKit(name).map(k -> kits.add(new CachedKit(name, k)));
        }

        data.addJson("ok", true, false);
        data.addJson("kits", kits, data.getQueryParam("details").isPresent());
    }


    @WebAPIEndpoint(method = HttpMethod.GET, path = "home/:uuid", perm = "home.list")
    public void getHomes(ServletData data, UUID uuid) {
        Optional<NucleusHomeService> optSrv = NucleusAPI.getHomeService();
        if (!optSrv.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Nuclues home service not available");
            return;
        }

        NucleusHomeService srv = optSrv.get();
        List<Home> homes = srv.getHomes(uuid);

        data.addJson("ok", true, false);
        data.addJson("homes", homes, data.getQueryParam("details").isPresent());
    }
}
