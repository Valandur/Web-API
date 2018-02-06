package valandur.webapi.integration.redprotect;

import br.net.fabiozumbi12.RedProtect.Sponge.RedProtect;
import br.net.fabiozumbi12.RedProtect.Sponge.Region;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.api.WebAPIAPI;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Permission;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Path("red-protect")
@Api(tags = { "Red Protect"}, value = "Create, edit and delete protected regions on your server")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class RedProtectServlet extends BaseServlet {

    public static void onRegister() {
        WebAPIAPI.getJsonService().ifPresent(srv -> {
            srv.registerCache(Region.class, CachedRegion.class);
        });
    }


    @GET
    @Path("/region")
    @Permission({ "region", "list" })
    @ApiOperation("List regions")
    public Collection<CachedRegion> getRegions(@QueryParam("world") ICachedWorld world) {
        Set<CachedRegion> regions;
        if (world != null) {
            regions = WebAPIAPI.runOnMain(() -> {
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
            regions = WebAPIAPI.runOnMain(() -> RedProtect.get().rm.getAllRegions().stream()
                    .map(CachedRegion::new)
                    .collect(Collectors.toSet()));
        }

        return regions;
    }

    @GET
    @Path("/region/{id}")
    @Permission({ "region", "one" })
    @ApiOperation("Get a region")
    public CachedRegion getRegion(@PathParam("id") String id)
            throws BadRequestException {
        if (!id.contains("@")) {
            throw new BadRequestException("Invalid region id");
        }

        return WebAPIAPI.runOnMain(() -> new CachedRegion(RedProtect.get().rm.getRegionById(id)));
    }

    @POST
    @Path("/region")
    @Permission({ "region", "create" })
    @ApiOperation(
            value = "Create a region", response = CachedRegion.class,
            notes = "Create a new region at a specified location")
    public Response createRegion(CachedRegion req)
            throws BadRequestException {
        CachedRegion resRegion = WebAPIAPI.runOnMain(() -> {
            String name = req.getName();
            if (name == null) {
                throw new BadRequestException("The region needs a name");
            }

            if (req.getWorld() == null) {
                throw new BadRequestException("The region needs a world");
            }
            Optional<World> optLive = req.getWorld().getLive();
            if (!optLive.isPresent()) {
                throw new InternalServerErrorException("Could not get live world");
            }

            World world = optLive.get();

            if (req.getMin() == null) {
                throw new BadRequestException("The region needs min coordinates");
            }
            Location<World> minLoc = new Location<>(world, req.getMin());

            if (req.getMax() == null) {
                throw new BadRequestException("The region needs max coordinates");
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
                throw new BadRequestException("The region needs a tpPoint");
            }
            Optional<Location> optLiveTpPoint = req.getTpPoint().getLive();
            if (!optLiveTpPoint.isPresent()) {
                throw new InternalServerErrorException("Could not get live world");
            }

            Region region = new Region(name, admins, members, leaders, maxLoc.getBlockX(), minLoc.getBlockX(),
                    maxLoc.getBlockZ(), minLoc.getBlockZ(), minLoc.getBlockY(), maxLoc.getBlockY(), flags, msg,
                    priority, world.getName(), null, 0, optLiveTpPoint.get(), canDelete);
            RedProtect.get().rm.add(region, world);
            return new CachedRegion(region);
        });

        return Response.created(URI.create(resRegion.getLink())).entity(resRegion).build();
    }

    @PUT
    @Path("/region/{id}")
    @Permission({ "region", "change" })
    @ApiOperation("Change a region")
    public CachedRegion changeRegion(@PathParam("id") String id, CachedRegion req)
            throws BadRequestException {
        if (!id.contains("@")) {
            throw new BadRequestException("Invalid region id");
        }

        return WebAPIAPI.runOnMain(() -> {
            Region region = RedProtect.get().rm.getRegionById(id);
            if (region == null) {
                throw new BadRequestException("Could not find region with id " + id);
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
    }

    @DELETE
    @Path("/region/{id}")
    @Permission({ "region", "delete" })
    @ApiOperation("Delete a region")
    public CachedRegion deleteRegion(@PathParam("id") String id)
            throws NotFoundException, BadRequestException {
        if (!id.contains("@")) {
            throw new BadRequestException("Invalid region id");
        }

        return WebAPIAPI.runOnMain(() -> {
            Region region = RedProtect.get().rm.getRegionById(id);
            if (region == null) {
                throw new NotFoundException("Could not find region with id " + id);
            }

            World world = Sponge.getServer().getWorld(region.getWorld()).orElse(null);
            RedProtect.get().rm.remove(region, world);

            return new CachedRegion(region);
        });
    }
}
