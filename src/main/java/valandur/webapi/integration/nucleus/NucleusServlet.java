package valandur.webapi.integration.nucleus;

import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import io.github.nucleuspowered.nucleus.api.nucleusdata.Kit;
import io.github.nucleuspowered.nucleus.api.nucleusdata.NamedLocation;
import io.github.nucleuspowered.nucleus.api.service.NucleusJailService;
import io.github.nucleuspowered.nucleus.api.service.NucleusKitService;
import org.eclipse.jetty.http.HttpMethod;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.api.WebAPIAPI;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Endpoint;
import valandur.webapi.api.servlet.IServletData;
import valandur.webapi.api.servlet.Servlet;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Servlet(basePath = "nucleus")
public class NucleusServlet extends BaseServlet {

    public static void onRegister() {
        WebAPIAPI.getJsonService().ifPresent(srv -> {
            srv.registerCache(NamedLocation.class, CachedNamedLocation.class);
            srv.registerCache(Kit.class, CachedKit.class);
        });
    }


    @Endpoint(method = HttpMethod.GET, path = "jail", perm = "jail.list")
    public void getJails(IServletData data) {
        Optional<NucleusJailService> optSrv = NucleusAPI.getJailService();
        if (!optSrv.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Nuclues jail service not available");
            return;
        }

        NucleusJailService srv = optSrv.get();

        Optional<List<CachedNamedLocation>> optRes = WebAPIAPI.runOnMain(
                () -> srv.getJails().values().stream().map(CachedNamedLocation::new).collect(Collectors.toList())
        );

        data.addData("ok", optRes.isPresent(), false);
        data.addData("jails", optRes.orElse(null), data.getQueryParam("details").isPresent());
    }

    @Endpoint(method = HttpMethod.GET, path = "jail/:name", perm = "jail.one")
    public void getJail(IServletData data, String name) {
        Optional<NucleusJailService> optSrv = NucleusAPI.getJailService();
        if (!optSrv.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Nuclues jail service not available");
            return;
        }

        NucleusJailService srv = optSrv.get();

        Optional<CachedNamedLocation> optRes = WebAPIAPI.runOnMain(() -> {
            Optional<NamedLocation> optJail = srv.getJail(name);
            if (!optJail.isPresent()) {
                data.sendError(HttpServletResponse.SC_NOT_FOUND, "Jail not found");
                return null;
            }

            return new CachedNamedLocation(optJail.get());
        });

        data.addData("ok", optRes.isPresent(), false);
        data.addData("jail", optRes.orElse(null), true);
    }

    @Endpoint(method = HttpMethod.POST, path = "jail", perm = "jail.create")
    public void createJail(IServletData data) {
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

        ICachedWorld world = req.getWorld().get();

        if (req.getPosition() == null) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "No valid position provided");
            return;
        }

        Optional<CachedNamedLocation> optRes = WebAPIAPI.runOnMain(() -> {
            Optional<World> optWorld = world.getLive();
            if (!optWorld.isPresent())
                return null;

            World w = optWorld.get();
            srv.setJail(req.getName(), new Location<>(w, req.getPosition()), req.getRotation());
            Optional<NamedLocation> optJail = srv.getJail(req.getName());
            return optJail.map(CachedNamedLocation::new).orElse(null);
        });

        data.setStatus(HttpServletResponse.SC_CREATED);
        data.addData("ok", optRes.isPresent(), false);
        data.addData("jail", optRes.orElse(null), true);
    }

    @Endpoint(method = HttpMethod.PUT, path = "jail/:name", perm = "jail.change")
    public void changeJail(IServletData data) {
        data.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Not implemented");
    }

    @Endpoint(method = HttpMethod.DELETE, path = "jail/:name", perm = "jail.delete")
    public void deleteJail(IServletData data, String name) {
        Optional<NucleusJailService> optSrv = NucleusAPI.getJailService();
        if (!optSrv.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Nuclues jail service not available");
            return;
        }

        NucleusJailService srv = optSrv.get();

        Optional<CachedNamedLocation> optRes = WebAPIAPI.runOnMain(() -> {
            Optional<NamedLocation> optJail = srv.getJail(name);
            if (!optJail.isPresent()) {
                data.sendError(HttpServletResponse.SC_NOT_FOUND, "Jail not found");
                return null;
            }

            srv.removeJail(name);

            return new CachedNamedLocation(optJail.get());
        });

        data.addData("ok", optRes.isPresent(), false);
        data.addData("jail", optRes.orElse(null), true);
    }


    @Endpoint(method = HttpMethod.GET, path = "kit", perm = "kit.list")
    public void getKits(IServletData data) {
        Optional<NucleusKitService> optSrv = NucleusAPI.getKitService();
        if (!optSrv.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Nuclues kit service not available");
            return;
        }

        NucleusKitService srv = optSrv.get();

        Optional<List<CachedKit>> kits = WebAPIAPI.runOnMain(
                () -> srv.getKitNames().stream()
                        .map(name -> srv.getKit(name).map(CachedKit::new).orElse(null))
                        .collect(Collectors.toList())
        );

        data.addData("ok", kits.isPresent(), false);
        data.addData("kits", kits.orElse(null), data.getQueryParam("details").isPresent());
    }

    @Endpoint(method = HttpMethod.GET, path = "kit/:name", perm = "kit.one")
    public void getKit(IServletData data, String name) {
        Optional<NucleusKitService> optSrv = NucleusAPI.getKitService();
        if (!optSrv.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Nuclues kit service not available");
            return;
        }

        NucleusKitService srv = optSrv.get();

        Optional<CachedKit> optRes = WebAPIAPI.runOnMain(() -> {
            Optional<Kit> optKit = srv.getKit(name);
            if (!optKit.isPresent()) {
                data.sendError(HttpServletResponse.SC_NOT_FOUND, "Kit not found");
                return null;
            }

            return new CachedKit(optKit.get());
        });

        data.addData("ok", optRes.isPresent(), false);
        data.addData("kit", optRes.orElse(null), true);
    }

    @Endpoint(method = HttpMethod.POST, path = "kit", perm = "kit.create")
    public void createKit(IServletData data) {
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

        Optional<CachedKit> resKit = WebAPIAPI.runOnMain(() -> {
            Kit kit = srv.createKit(req.getName());
            kit.setCost(req.getCost());
            kit.setCooldown(req.getCooldown());
            if (req.hasStacks()) {
                try {
                    kit.setStacks(req.getStacks());
                } catch (Exception e) {
                    data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not process item stack: " + e.getMessage());
                    return null;
                }
            }
            if (req.hasCommands()) {
                kit.setCommands(req.getCommands());
            }
            srv.saveKit(kit);
            return new CachedKit(kit);
        });

        data.addData("ok", resKit.isPresent(), false);
        data.addData("kit", resKit.orElse(null), true);
    }

    @Endpoint(method = HttpMethod.PUT, path = "kit/:name", perm = "kit.change")
    public void changeKit(IServletData data, String name) {
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

        Optional<CachedKit> optRes = WebAPIAPI.runOnMain(() -> {
            Optional<Kit> optKit = srv.getKit(name);
            if (!optKit.isPresent()) {
                data.sendError(HttpServletResponse.SC_NOT_FOUND, "Kit not found");
                return null;
            }

            Kit kit = optKit.get();
            if (req.hasCost()) {
                kit.setCost(req.getCost());
            }
            if (req.hasCooldown()) {
                kit.setCooldown(req.getCooldown());
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

            srv.saveKit(kit);

            return new CachedKit(kit);
        });

        data.addData("ok", optRes.isPresent(), false);
        data.addData("kit", optRes.orElse(null), true);
    }

    @Endpoint(method = HttpMethod.DELETE, path = "kit/:name", perm = "kit.delete")
    public void deleteKit(IServletData data, String name) {
        Optional<NucleusKitService> optSrv = NucleusAPI.getKitService();
        if (!optSrv.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Nuclues kit service not available");
            return;
        }

        NucleusKitService srv = optSrv.get();

        Optional<CachedKit> optRes = WebAPIAPI.runOnMain(() -> {
            Optional<Kit> optKit = srv.getKit(name);
            if (!optKit.isPresent()) {
                data.sendError(HttpServletResponse.SC_NOT_FOUND, "Kit not found");
                return null;
            }

            srv.removeKit(name);
            return new CachedKit(optKit.get());
        });

        data.addData("ok", optRes.isPresent(), false);
        data.addData("kit", optRes.orElse(null), true);
    }
}
