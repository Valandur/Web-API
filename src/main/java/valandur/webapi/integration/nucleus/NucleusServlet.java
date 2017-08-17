package valandur.webapi.integration.nucleus;

import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import io.github.nucleuspowered.nucleus.api.nucleusdata.Home;
import io.github.nucleuspowered.nucleus.api.nucleusdata.Kit;
import io.github.nucleuspowered.nucleus.api.nucleusdata.NamedLocation;
import io.github.nucleuspowered.nucleus.api.service.NucleusHomeService;
import io.github.nucleuspowered.nucleus.api.service.NucleusJailService;
import io.github.nucleuspowered.nucleus.api.service.NucleusKitService;
import io.github.nucleuspowered.nucleus.api.service.NucleusServerShopService;
import org.eclipse.jetty.http.HttpMethod;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;
import valandur.webapi.api.annotation.WebAPIEndpoint;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.servlet.WebAPIBaseServlet;
import valandur.webapi.json.JsonService;
import valandur.webapi.servlet.ServletData;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

        Optional<List<NamedLocation>> optRes = WebAPI.runOnMain(() -> new ArrayList<>(srv.getJails().values()));

        data.addJson("ok", optRes.isPresent(), false);
        data.addJson("jails", optRes.orElse(null), data.getQueryParam("details").isPresent());
    }

    @WebAPIEndpoint(method = HttpMethod.GET, path = "jail/:name", perm = "jail.one")
    public void getJail(ServletData data, String name) {
        Optional<NucleusJailService> optSrv = NucleusAPI.getJailService();
        if (!optSrv.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Nuclues jail service not available");
            return;
        }

        NucleusJailService srv = optSrv.get();

        Optional<NamedLocation> optRes = WebAPI.runOnMain(() -> {
            Optional<NamedLocation> optJail = srv.getJail(name);
            if (!optJail.isPresent()) {
                data.sendError(HttpServletResponse.SC_NOT_FOUND, "Jail not found");
                return null;
            }

            return optJail.get();
        });

        data.addJson("ok", optRes.isPresent(), false);
        data.addJson("jail", optRes.orElse(null), true);
    }

    @WebAPIEndpoint(method = HttpMethod.POST, path = "jail", perm = "jail.create")
    public void createJail(ServletData data) {
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

        Optional<NamedLocation> optRes = WebAPI.runOnMain(() -> {
            Optional<?> optWorld = req.getWorld().get().getLive();
            if (!optWorld.isPresent())
                return null;

            World w = (World)optWorld.get();
            srv.setJail(req.getName(), new Location<>(w, req.getPosition()), req.getRotation());
            return srv.getJail(req.getName()).orElse(null);
        });

        data.setStatus(HttpServletResponse.SC_CREATED);
        data.addJson("ok", optRes.isPresent(), false);
        data.addJson("jail", optRes.orElse(null), true);
    }

    @WebAPIEndpoint(method = HttpMethod.PUT, path = "jail/:name", perm = "jail.change")
    public void changeJail(ServletData data) {
        data.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Not implemented");
    }

    @WebAPIEndpoint(method = HttpMethod.DELETE, path = "jail/:name", perm = "jail.delete")
    public void deleteJail(ServletData data, String name) {
        Optional<NucleusJailService> optSrv = NucleusAPI.getJailService();
        if (!optSrv.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Nuclues jail service not available");
            return;
        }

        NucleusJailService srv = optSrv.get();

        Optional<NamedLocation> optRes = WebAPI.runOnMain(() -> {
            Optional<NamedLocation> optJail = srv.getJail(name);
            if (!optJail.isPresent()) {
                data.sendError(HttpServletResponse.SC_NOT_FOUND, "Jail not found");
                return null;
            }

            srv.removeJail(name);

            return optJail.get();
        });

        data.addJson("ok", optRes.isPresent(), false);
        data.addJson("jail", optRes.orElse(null), true);
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
        WebAPI.runOnMain(() -> {
            for (String name : srv.getKitNames()) {
                srv.getKit(name).map(k -> kits.add(new CachedKit(name, k)));
            }
        });

        data.addJson("ok", true, false);
        data.addJson("kits", kits, data.getQueryParam("details").isPresent());
    }

    @WebAPIEndpoint(method = HttpMethod.GET, path = "kit/:name", perm = "kit.one")
    public void getKit(ServletData data, String name) {
        Optional<NucleusKitService> optSrv = NucleusAPI.getKitService();
        if (!optSrv.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Nuclues kit service not available");
            return;
        }

        NucleusKitService srv = optSrv.get();

        Optional<CachedKit> optRes = WebAPI.runOnMain(() -> {
            Optional<Kit> optKit = srv.getKit(name);
            if (!optKit.isPresent()) {
                data.sendError(HttpServletResponse.SC_NOT_FOUND, "Kit not found");
                return null;
            }

            return new CachedKit(name, optKit.get());
        });

        data.addJson("ok", optRes.isPresent(), false);
        data.addJson("kit", optRes.orElse(null), true);
    }

    @WebAPIEndpoint(method = HttpMethod.POST, path = "kit", perm = "kit.create")
    public void createKit(ServletData data) {
        Optional<NucleusKitService> optSrv = NucleusAPI.getKitService();
        if (!optSrv.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Nuclues kit service not available");
            return;
        }

        NucleusKitService srv = optSrv.get();

        Optional<CreateKitRequest> optReq = data.getRequestBody(CreateKitRequest.class);
        if (!optReq.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid kit data: " + data.getLastParseError().getMessage());
            return;
        }

        CreateKitRequest req = optReq.get();

        if (req.getName().isEmpty()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid kit name");
            return;
        }

        Optional<CachedKit> resKit = WebAPI.runOnMain(() -> {
            Kit kit = srv.createKit();
            kit.setCost(req.getCost());
            kit.setInterval(req.getInterval());
            try {
                kit.setStacks(req.getStacks());
            } catch (Exception e) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not process item stack: " + e.getMessage());
                return null;
            }
            kit.setCommands(req.getCommands());
            srv.saveKit(req.getName(), kit);
            return new CachedKit(req.getName(), kit);
        });

        data.addJson("ok", resKit.isPresent(), false);
        data.addJson("kit", resKit.orElse(null), true);
    }

    @WebAPIEndpoint(method = HttpMethod.PUT, path = "kit/:name", perm = "kit.change")
    public void changeKit(ServletData data, String name) {
        Optional<NucleusKitService> optSrv = NucleusAPI.getKitService();
        if (!optSrv.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Nuclues kit service not available");
            return;
        }

        NucleusKitService srv = optSrv.get();

        Optional<BaseKitRequest> optReq = data.getRequestBody(BaseKitRequest.class);
        if (!optReq.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid kit data: " + data.getLastParseError().getMessage());
            return;
        }

        BaseKitRequest req = optReq.get();

        Optional<CachedKit> optRes = WebAPI.runOnMain(() -> {
            Optional<Kit> optKit = srv.getKit(name);
            if (!optKit.isPresent()) {
                data.sendError(HttpServletResponse.SC_NOT_FOUND, "Kit not found");
                return null;
            }

            Kit kit = optKit.get();
            if (req.hasCost()) {
                kit.setCost(req.getCost());
            }
            if (req.hasInterval()) {
                kit.setInterval(req.getInterval());
            }
            if (req.hasCommands()) {
                kit.setCommands(req.getCommands());
            }
            if (req.hasStacks()) {
                try {
                    kit.setStacks(req.getStacks());
                } catch (Exception e) {
                    data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not process item stack: " + e.getMessage());
                    return null;
                }
            }

            return new CachedKit(name, kit);
        });

        data.addJson("ok", optRes.isPresent(), false);
        data.addJson("kit", optRes.orElse(null), true);
    }

    @WebAPIEndpoint(method = HttpMethod.DELETE, path = "kit/:name", perm = "kit.delete")
    public void deleteKit(ServletData data, String name) {
        Optional<NucleusKitService> optSrv = NucleusAPI.getKitService();
        if (!optSrv.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Nuclues kit service not available");
            return;
        }

        NucleusKitService srv = optSrv.get();

        Optional<CachedKit> optRes = WebAPI.runOnMain(() -> {
            Optional<Kit> optKit = srv.getKit(name);
            if (!optKit.isPresent()) {
                data.sendError(HttpServletResponse.SC_NOT_FOUND, "Kit not found");
                return null;
            }

            srv.removeKit(name);
            return new CachedKit(name, optKit.get());
        });

        data.addJson("ok", optRes.isPresent(), false);
        data.addJson("kit", optRes.orElse(null), true);
    }


    @WebAPIEndpoint(method = HttpMethod.GET, path = "home/:uuid", perm = "home.list")
    public void getHomes(ServletData data, UUID uuid) {
        Optional<NucleusHomeService> optSrv = NucleusAPI.getHomeService();
        if (!optSrv.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Nuclues home service not available");
            return;
        }

        NucleusHomeService srv = optSrv.get();

        Optional<List<Home>> optRes = WebAPI.runOnMain(() -> {
            return srv.getHomes(uuid);
        });

        data.addJson("ok", optRes.isPresent(), false);
        data.addJson("homes", optRes.orElse(null), true);
    }


    @WebAPIEndpoint(method = HttpMethod.GET, path = "shop", perm = "shop.list")
    public void getShops(ServletData data) {
        Optional<NucleusServerShopService> optSrv = NucleusAPI.getServerShopService();
        if (!optSrv.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Nuclues server shop service not available");
            return;
        }

        NucleusServerShopService srv = optSrv.get();

        Map<String, Double> buyPrices = new ConcurrentHashMap<>();
        Map<String, Double> sellPrices = new ConcurrentHashMap<>();
        WebAPI.runOnMain(() -> {
            for (Map.Entry<CatalogType, Double> entry : srv.getBuyPrices().entrySet()) {
                buyPrices.put(entry.getKey().getId(), entry.getValue());
            }

            for (Map.Entry<CatalogType, Double> entry : srv.getSellPrices().entrySet()) {
                sellPrices.put(entry.getKey().getId(), entry.getValue());
            }
        });

        data.addJson("ok", true, false);
        data.addJson("buy", buyPrices, true);
        data.addJson("sell", sellPrices, true);
    }
}
