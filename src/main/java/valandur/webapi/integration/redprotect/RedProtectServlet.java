package valandur.webapi.integration.redprotect;

import br.net.fabiozumbi12.RedProtect.Sponge.RedProtect;
import br.net.fabiozumbi12.RedProtect.Sponge.Region;
import org.eclipse.jetty.http.HttpMethod;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.api.WebAPIAPI;
import valandur.webapi.api.cache.player.ICachedPlayer;
import valandur.webapi.api.cache.world.CachedLocation;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Endpoint;
import valandur.webapi.api.servlet.IServletData;
import valandur.webapi.api.servlet.Servlet;
import valandur.webapi.cache.player.CachedPlayer;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Servlet(basePath = "redprotect")
public class RedProtectServlet extends BaseServlet {

    public static void onRegister() {
        WebAPIAPI.getJsonService().ifPresent(srv -> {
            srv.registerCache(Region.class, CachedRegion.class);
        });
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
                Optional<World> optLive = world.getLive();
                if (!optLive.isPresent()) {
                    return null;
                }

                World live = optLive.get();
                return RedProtect.get().rm.getRegionsByWorld(live).stream()
                        .map(CachedRegion::new)
                        .collect(Collectors.toSet());
            });
        } else {
            optRegions = WebAPIAPI.runOnMain(() -> RedProtect.get().rm.getAllRegions().stream()
                    .map(CachedRegion::new)
                    .collect(Collectors.toSet()));
        }

        data.addData("ok", optRegions.isPresent(), false);
        data.addData("regions", optRegions.orElse(null), data.getQueryParam("details").isPresent());
    }

    @Endpoint(method = HttpMethod.GET, path = "region/:id", perm = "one")
    public void getRegion(IServletData data, String id) {
        if (!id.contains("@")) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid region id");
            return;
        }

        Optional<CachedRegion> optRegion = WebAPIAPI.runOnMain(() -> new CachedRegion(RedProtect.get().rm.getRegionById(id)));

        data.addData("ok", optRegion.isPresent(), false);
        data.addData("region", optRegion.orElse(null), true);
    }

    @Endpoint(method = HttpMethod.POST, path = "region", perm = "create")
    public void createRegion(IServletData data) {
        Optional<CachedRegion> optReq = data.getRequestBody(CachedRegion.class);
        if (!optReq.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid region data: " +
                    data.getLastParseError().getMessage());
            return;
        }

        CachedRegion req = optReq.get();

        Optional<CachedRegion> optRegion = WebAPIAPI.runOnMain(() -> {
            String name = req.getName();
            if (name == null) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "The region needs a name");
                return null;
            }

            if (req.getWorld() == null) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "The region needs a world");
                return null;
            }
            Optional<World> optLive = req.getWorld().getLive();
            if (!optLive.isPresent()) {
                data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not get live world");
                return null;
            }

            World world = optLive.get();

            if (req.getMin() == null) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "The region needs min coordinates");
                return null;
            }
            Location<World> minLoc = new Location<>(world, req.getMin());

            if (req.getMax() == null) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "The region needs max coordinates");
                return null;
            }
            Location<World> maxLoc = new Location<>(world, req.getMax());

            List<String> leaders;
            if (req.getLeaders() == null) {
                leaders = new ArrayList<>();
            } else {
                leaders = req.getLeaders().stream()
                        .filter(Objects::nonNull)
                        .map(p -> p.getUUID().toString())
                        .collect(Collectors.toList());
            }

            List<String> admins;
            if (req.getAdmins() == null) {
                admins = new ArrayList<>();
            } else {
                admins = req.getAdmins().stream()
                        .filter(Objects::nonNull)
                        .map(p -> p.getUUID().toString())
                        .collect(Collectors.toList());
            }

            List<String> members;
            if (req.getMembers() == null) {
                members = new ArrayList<>();
            } else {
                members = req.getMembers().stream()
                        .filter(Objects::nonNull)
                        .map(p -> p.getUUID().toString())
                        .collect(Collectors.toList());
            }

            HashMap<String, Object> flags = req.getFlags();
            if (flags == null) {
                flags = new HashMap<>();
            }

            Boolean canDelete = req.getCanDelete();
            if (canDelete == null) {
                canDelete = true;
            }

            String msg = req.getWelcomeMessage();
            int priority = req.getPriority() != null ? req.getPriority() : 0;

            if (req.getTpPoint() == null) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "The region needs a tpPoint");
                return null;
            }
            Optional<Location> optLiveTpPoint = req.getTpPoint().getLive();
            if (!optLiveTpPoint.isPresent()) {
                data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not get live world");
                return null;
            }

            Region region = new Region(name, admins, members, leaders, maxLoc.getBlockX(), minLoc.getBlockX(),
                    maxLoc.getBlockZ(), minLoc.getBlockZ(), minLoc.getBlockY(), maxLoc.getBlockY(), flags, msg,
                    priority, world.getName(), null, 0, optLiveTpPoint.get(), canDelete);
            RedProtect.get().rm.add(region, world);
            return new CachedRegion(region);
        });

        data.addData("ok", optRegion.isPresent(), false);
        data.addData("region", optRegion.orElse(null), true);
    }

    @Endpoint(method = HttpMethod.PUT, path = "region/:id", perm = "change")
    public void changeRegion(IServletData data, String id) {
        if (!id.contains("@")) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid region id");
            return;
        }

        Optional<CachedRegion> optReq = data.getRequestBody(CachedRegion.class);
        if (!optReq.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid region data: " +
                    data.getLastParseError().getMessage());
            return;
        }

        CachedRegion req = optReq.get();

        Optional<CachedRegion> optRegion = WebAPIAPI.runOnMain(() -> {
            Region region = RedProtect.get().rm.getRegionById(id);
            if (region == null) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not find region with id " + id);
                return null;
            }

            if (req.getLeaders() != null) {
                region.setLeaders(req.getLeaders().stream()
                        .filter(Objects::nonNull)
                        .map(p -> p.getUUID().toString())
                        .collect(Collectors.toList()));
            }

            if (req.getAdmins() != null) {
                region.setAdmins(req.getAdmins().stream()
                        .filter(Objects::nonNull)
                        .map(p -> p.getUUID().toString())
                        .collect(Collectors.toList()));
            }

            if (req.getMembers() != null) {
                region.setMembers(req.getMembers().stream()
                        .filter(Objects::nonNull)
                        .map(p -> p.getUUID().toString())
                        .collect(Collectors.toList()));
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

            if (req.getWorld() != null) {
                region.setWorld(req.getWorld().getName());
            }

            if (req.getTpPoint() != null) {
                Optional<Location> optLoc = req.getTpPoint().getLive();
                if (!optLoc.isPresent()) {
                    return null;
                }

                region.setTPPoint(optLoc.get());
            }

            if (req.getCanDelete() != null) {
                region.setCanDelete(req.getCanDelete());
            }

            return new CachedRegion(region);
        });

        data.addData("ok", optRegion.isPresent(), false);
        data.addData("region", optRegion.orElse(null), true);
    }

    @Endpoint(method = HttpMethod.DELETE, path = "region/:id", perm = "delete")
    public void deleteRegion(IServletData data, String id) {
        if (!id.contains("@")) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid region id");
            return;
        }

        Optional<CachedRegion> optRegion = WebAPIAPI.runOnMain(() -> {
            Region region = RedProtect.get().rm.getRegionById(id);
            if (region == null) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not find region with id " + id);
                return null;
            }

            World world = Sponge.getServer().getWorld(region.getWorld()).orElse(null);
            RedProtect.get().rm.remove(region, world);

            return new CachedRegion(region);
        });

        data.addData("ok", optRegion.isPresent(), false);
        data.addData("region", optRegion.orElse(null), true);
    }
}
