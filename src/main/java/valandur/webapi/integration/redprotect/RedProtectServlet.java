package valandur.webapi.integration.redprotect;

import br.net.fabiozumbi12.redprotect.RedProtect;
import br.net.fabiozumbi12.redprotect.Region;
import org.eclipse.jetty.http.HttpMethod;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.api.WebAPIAPI;
import valandur.webapi.api.cache.world.CachedLocation;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.api.json.IJsonService;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Endpoint;
import valandur.webapi.api.servlet.IServletData;
import valandur.webapi.api.servlet.Servlet;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Servlet(basePath = "redprotect")
public class RedProtectServlet extends BaseServlet {

    public static void onRegister() {
        IJsonService json = WebAPIAPI.getJsonService().get();
        json.registerCache(Region.class, CachedRegion.class);
    }


    @Endpoint(method = HttpMethod.GET, path = "region", perm = "list")
    public void getRegions(IServletData data) {
        Optional<String> worldUuid = data.getQueryParam("world");

        Optional<Set<CachedRegion>> optRegions;
        if (worldUuid.isPresent()) {
            Optional<ICachedWorld> optWorld = cacheService.getWorld(worldUuid.get());
            if (!optWorld.isPresent()) {
                data.sendError(HttpServletResponse.SC_NOT_FOUND, "Could not find world " + worldUuid.get());
                return;
            }

            ICachedWorld world = optWorld.get();
            optRegions = WebAPIAPI.runOnMain(() -> {
                Optional<?> optLive = world.getLive();
                if (!optLive.isPresent()) {
                    return null;
                }

                World live = (World)optLive.get();
                return RedProtect.rm.getRegionsByWorld(live).stream()
                        .map(CachedRegion::new)
                        .collect(Collectors.toSet());
            });
        } else {
            optRegions = WebAPIAPI.runOnMain(() -> RedProtect.rm.getAllRegions().stream()
                    .map(CachedRegion::new)
                    .collect(Collectors.toSet()));
        }

        data.addJson("ok", optRegions.isPresent(), false);
        data.addJson("regions", optRegions.orElse(null), data.getQueryParam("details").isPresent());
    }

    @Endpoint(method = HttpMethod.GET, path = "region/:id", perm = "one")
    public void getRegion(IServletData data, String id) {
        if (!id.contains("@")) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid region id");
            return;
        }

        Optional<CachedRegion> optRegion = WebAPIAPI.runOnMain(() -> new CachedRegion(RedProtect.rm.getRegionById(id)));

        data.addJson("ok", optRegion.isPresent(), false);
        data.addJson("region", optRegion.orElse(null), true);
    }

    @Endpoint(method = HttpMethod.POST, path = "region", perm = "create")
    public void createRegion(IServletData data) {
        Optional<CreateRegionRequest> optReq = data.getRequestBody(CreateRegionRequest.class);
        if (!optReq.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid region data: " + data.getLastParseError().getMessage());
            return;
        }

        CreateRegionRequest req = optReq.get();

        Optional<CachedRegion> optRegion = WebAPIAPI.runOnMain(() -> {
            String name = req.getName();
            if (name == null) {
                return null;
            }

            Optional<ICachedWorld> optWorld = req.getWorld();
            if (!optWorld.isPresent()) {
                return null;
            }

            Optional<?> optLive = optWorld.get().getLive();
            if (!optLive.isPresent()) {
                return null;
            }

            World world = (World)optLive.get();

            if (req.getMinLoc() == null) {
                return null;
            }
            Location<World> minLoc = new Location<>(world, req.getMinLoc());

            if (req.getMaxLoc() == null) {
                return null;
            }
            Location<World> maxloc = new Location<>(world, req.getMaxLoc());

            List<String> leaders = req.getLeaders();
            if (leaders == null) {
                leaders = new ArrayList<>();
            }

            List<String> admins = req.getAdmins();
            if (admins == null) {
                admins = new ArrayList<>();
            }

            List<String> members = req.getMembers();
            if (members == null) {
                members = new ArrayList<>();
            }

            HashMap<String, Object> flags = req.getFlags();
            if (flags == null) {
                flags = new HashMap<>();
            }

            Boolean canDelete = req.canDelete();
            if (canDelete == null) {
                canDelete = true;
            }

            String msg = req.getWelcomeMessage();
            int priority = req.getPriority();

            Location<World> tpPoint = null;
            Optional<CachedLocation> optTpPoint = req.getTpPos();
            if (optTpPoint.isPresent()) {
                Optional<?> optLiveTpPoint = optTpPoint.get().getLive();
                if (!optLiveTpPoint.isPresent()) {
                    return null;
                }

                tpPoint = (Location<World>)optLiveTpPoint.get();
            }

            Region region = new Region(name, admins, members, leaders, minLoc, maxloc, flags, msg,
                    priority, world.getName(), null, 0, tpPoint, canDelete);
            RedProtect.rm.add(region, world);
            return new CachedRegion(region);
        });

        data.addJson("ok", optRegion.isPresent(), false);
        data.addJson("region", optRegion.orElse(null), true);
    }

    @Endpoint(method = HttpMethod.PUT, path = "region/:id", perm = "change")
    public void changeRegion(IServletData data, String id) {
        if (!id.contains("@")) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid region id");
            return;
        }

        Optional<BaseRegionRequest> optReq = data.getRequestBody(BaseRegionRequest.class);
        if (!optReq.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid region data: " + data.getLastParseError().getMessage());
            return;
        }

        BaseRegionRequest req = optReq.get();

        Optional<CachedRegion> optRegion = WebAPIAPI.runOnMain(() -> {
            Region region = RedProtect.rm.getRegionById(id);
            if (region == null) {
                return null;
            }

            if (req.getLeaders() != null) {
                region.setLeaders(req.getLeaders());
            }

            if (req.getAdmins() != null) {
                region.setAdmins(req.getAdmins());
            }

            if (req.getMembers() != null) {
                region.setMembers(req.getMembers());
            }

            if (req.getPriority() != null) {
                region.setPrior(req.getPriority());
            }

            if (req.getWelcomeMessage() != null) {
                region.setWelcome(req.getWelcomeMessage());
            }

            if (req.getFlags() != null) {
                for (Map.Entry<String, Object> entry : req.getFlags().entrySet()) {
                    region.setFlag(entry.getKey(), entry.getValue());
                }
            }

            if (req.getWorld().isPresent()) {
                region.setWorld(req.getWorld().get().getName());
            }

            Optional<CachedLocation> optPos = req.getTpPos();
            if (optPos.isPresent()) {
                Optional<Location> optLoc = optPos.get().getLive();
                if (!optLoc.isPresent()) {
                    return null;
                }

                Location<World> loc = (Location<World>)optLoc.get();
                region.setTPPoint(loc);
            }

            if (req.canDelete() != null) {
                region.setCanDelete(req.canDelete());
            }

            return new CachedRegion(region);
        });

        data.addJson("ok", optRegion.isPresent(), false);
        data.addJson("region", optRegion.orElse(null), true);
    }

    @Endpoint(method = HttpMethod.DELETE, path = "region/:id", perm = "delete")
    public void deleteRegion(IServletData data, String id) {
        if (!id.contains("@")) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid region id");
            return;
        }

        Optional<CachedRegion> optRegion = WebAPIAPI.runOnMain(() -> {
            Region region = RedProtect.rm.getRegionById(id);
            if (region == null) {
                return null;
            }

            World world = Sponge.getServer().getWorld(region.getWorld()).orElse(null);
            RedProtect.rm.remove(region, world);

            return new CachedRegion(region);
        });

        data.addJson("ok", optRegion.isPresent(), false);
        data.addJson("region", optRegion.orElse(null), true);
    }
}
