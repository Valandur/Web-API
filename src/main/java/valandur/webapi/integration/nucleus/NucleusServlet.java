package valandur.webapi.integration.nucleus;

import com.flowpowered.math.vector.Vector3d;
import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import io.github.nucleuspowered.nucleus.api.nucleusdata.Kit;
import io.github.nucleuspowered.nucleus.api.nucleusdata.NamedLocation;
import io.github.nucleuspowered.nucleus.api.service.NucleusJailService;
import io.github.nucleuspowered.nucleus.api.service.NucleusKitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.spongepowered.api.world.Location;
import valandur.webapi.api.WebAPIAPI;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Permission;
import valandur.webapi.handler.NotImplementedException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("nucleus")
@Api(tags = { "Nucleus" }, value = "Manage your kits and jails")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class NucleusServlet extends BaseServlet {

    public static void onRegister() {
        WebAPIAPI.getJsonService().ifPresent(srv -> {
            srv.registerCache(NamedLocation.class, CachedNamedLocation.class);
            srv.registerCache(Kit.class, CachedKit.class);
        });
    }


    // Jails
    @GET
    @Path("/jail")
    @Permission({ "jail", "list" })
    @ApiOperation(value = "List jails", notes = "Get a list of all the jails on the server.")
    public Collection<CachedNamedLocation> getJails()
            throws InternalServerErrorException {
        Optional<NucleusJailService> optSrv = NucleusAPI.getJailService();
        if (!optSrv.isPresent()) {
            throw new InternalServerErrorException("Nuclues jail service not available");
        }

        NucleusJailService srv = optSrv.get();

        Optional<List<CachedNamedLocation>> optRes = WebAPIAPI.runOnMain(
                () -> srv.getJails().values().stream().map(CachedNamedLocation::new).collect(Collectors.toList())
        );

        return optRes.orElse(null);
    }

    @GET
    @Path("/jail/{name}")
    @Permission({ "jail", "one" })
    @ApiOperation(value = "Get a jail", notes = "Get detailed information about a jail.")
    public CachedNamedLocation getJail(@PathParam("name") String name)
            throws InternalServerErrorException, NotFoundException {
        Optional<NucleusJailService> optSrv = NucleusAPI.getJailService();
        if (!optSrv.isPresent()) {
            throw new InternalServerErrorException("Nuclues jail service not available");
        }

        NucleusJailService srv = optSrv.get();

        Optional<CachedNamedLocation> optRes = WebAPIAPI.runOnMain(() -> {
            Optional<NamedLocation> optJail = srv.getJail(name);
            if (!optJail.isPresent()) {
                throw new NotFoundException("Jail with name " + name + " not found");
            }

            return new CachedNamedLocation(optJail.get());
        });

        return optRes.orElse(null);
    }

    @POST
    @Path("/jail")
    @Permission({ "jail", "create" })
    @ApiOperation(value = "Create a jail", notes = "Creates a new jail.")
    public CachedNamedLocation createJail(CachedNamedLocation req)
            throws BadRequestException, InternalServerErrorException {
        Optional<NucleusJailService> optSrv = NucleusAPI.getJailService();
        if (!optSrv.isPresent()) {
            throw new InternalServerErrorException("Nuclues jail service not available");
        }

        NucleusJailService srv = optSrv.get();

        if (req.getLocation() == null) {
            throw new BadRequestException("A location is required");
        }

        Optional<CachedNamedLocation> optRes = WebAPIAPI.runOnMain(() -> {
            Optional<Location> optLive = req.getLocation().getLive();
            if (!optLive.isPresent()) {
                throw new InternalServerErrorException("Could not get live location");
            }
            Vector3d rot = req.getRotation() == null ? Vector3d.FORWARD : req.getRotation();
            srv.setJail(req.getName(), optLive.get(), rot);
            Optional<NamedLocation> optJail = srv.getJail(req.getName());
            return optJail.map(CachedNamedLocation::new).orElse(null);
        });

        return optRes.orElse(null);
    }

    @PUT
    @Path("/jail/{name}")
    @Permission({ "jail", "change" })
    @ApiOperation(value = "Change a jail", notes = "Change an existing jail.")
    public CachedNamedLocation changeJail()
            throws NotImplementedException {
        throw new NotImplementedException();
    }

    @DELETE
    @Path("/jail/{name}")
    @Permission({ "jail", "delete" })
    @ApiOperation(value = "Delete a jail", notes = "Delete an existing jail.")
    public CachedNamedLocation deleteJail(@PathParam("name") String name)
            throws InternalServerErrorException, NotFoundException {
        Optional<NucleusJailService> optSrv = NucleusAPI.getJailService();
        if (!optSrv.isPresent()) {
            throw new InternalServerErrorException("Nuclues jail service not available");
        }

        NucleusJailService srv = optSrv.get();

        Optional<CachedNamedLocation> optRes = WebAPIAPI.runOnMain(() -> {
            Optional<NamedLocation> optJail = srv.getJail(name);
            if (!optJail.isPresent()) {
                throw new NotFoundException("Jail with name " + name + " not found");
            }

            srv.removeJail(name);

            return new CachedNamedLocation(optJail.get());
        });

        return optRes.orElse(null);
    }


    // Tickets
    @GET
    @Path("/kit")
    @Permission({ "kit", "list" })
    @ApiOperation(value = "List kits", notes = "Get a list of all the kits on the server.")
    public Collection<CachedKit> getKits()
            throws InternalServerErrorException {
        Optional<NucleusKitService> optSrv = NucleusAPI.getKitService();
        if (!optSrv.isPresent()) {
            throw new InternalServerErrorException("Nuclues kit service not available");
        }

        NucleusKitService srv = optSrv.get();

        Optional<List<CachedKit>> kits = WebAPIAPI.runOnMain(
                () -> srv.getKitNames().stream()
                        .map(name -> srv.getKit(name).map(CachedKit::new).orElse(null))
                        .collect(Collectors.toList())
        );

        return kits.orElse(null);
    }

    @GET
    @Path("/kit/{name}")
    @Permission({ "kit", "one" })
    @ApiOperation(value = "Get a kit", notes = "Get detailed information about a kit.")
    public CachedKit getKit(@PathParam("name") String name)
            throws InternalServerErrorException, NotFoundException {
        Optional<NucleusKitService> optSrv = NucleusAPI.getKitService();
        if (!optSrv.isPresent()) {
            throw new InternalServerErrorException("Nuclues kit service not available");
        }

        NucleusKitService srv = optSrv.get();

        Optional<CachedKit> optRes = WebAPIAPI.runOnMain(() -> {
            Optional<Kit> optKit = srv.getKit(name);
            if (!optKit.isPresent()) {
                throw new NotFoundException("Kit with name " + name + " not found");
            }

            return new CachedKit(optKit.get());
        });

        return optRes.orElse(null);
    }

    @POST
    @Path("/kit")
    @Permission({ "kit", "create" })
    @ApiOperation(value = "Create a kit", notes = "Creates a new kit.")
    public CachedKit createKit(CachedKit req)
            throws InternalServerErrorException, BadRequestException {
        Optional<NucleusKitService> optSrv = NucleusAPI.getKitService();
        if (!optSrv.isPresent()) {
            throw new InternalServerErrorException("Nuclues kit service not available");
        }

        NucleusKitService srv = optSrv.get();

        if (req.getName().isEmpty()) {
            throw new BadRequestException("Invalid kit name");
        }

        Optional<CachedKit> resKit = WebAPIAPI.runOnMain(() -> {
            Kit kit = srv.createKit(req.getName());
            kit.setCost(req.getCost());
            kit.setCooldown(Duration.ofMillis(req.getCooldown()));
            if (req.getStacks() != null) {
                try {
                    kit.setStacks(req.getStacks());
                } catch (Exception e) {
                    throw new BadRequestException("Could not process item stack: " + e.getMessage());
                }
            }
            if (req.getCommands() != null) {
                kit.setCommands(req.getCommands());
            }
            srv.saveKit(kit);
            return new CachedKit(kit);
        });

        return resKit.orElse(null);
    }

    @PUT
    @Path("/kit/{name}")
    @Permission({ "kit", "chang" })
    @ApiOperation(value = "Change a kit", notes = "Change an existing kit.")
    public CachedKit changeKit(@PathParam("name") String name, CachedKit req)
            throws InternalServerErrorException, NotFoundException, BadRequestException {
        Optional<NucleusKitService> optSrv = NucleusAPI.getKitService();
        if (!optSrv.isPresent()) {
            throw new InternalServerErrorException("Nuclues kit service not available");
        }

        NucleusKitService srv = optSrv.get();

        Optional<CachedKit> optRes = WebAPIAPI.runOnMain(() -> {
            Optional<Kit> optKit = srv.getKit(name);
            if (!optKit.isPresent()) {
                throw new NotFoundException("Kit with name " + name + " not found");
            }

            Kit kit = optKit.get();
            if (req.getCost() != null) {
                kit.setCost(req.getCost());
            }
            if (req.getCooldown() != null) {
                kit.setCooldown(Duration.ofMillis(req.getCooldown()));
            }
            if (req.getCommands() != null) {
                kit.setCommands(req.getCommands());
            }
            if (req.getStacks() != null) {
                try {
                    kit.setStacks(req.getStacks());
                } catch (Exception e) {
                    throw new BadRequestException("Could not process item stack: " + e.getMessage());
                }
            }

            srv.saveKit(kit);

            return new CachedKit(kit);
        });

        return optRes.orElse(null);
    }

    @DELETE
    @Path("/kit/{name}")
    @Permission({ "kit", "delete" })
    @ApiOperation(value = "Delete a kit", notes = "Delete an existing kit.")
    public CachedKit deleteKit(@PathParam("name") String name)
            throws InternalServerErrorException, NotFoundException {
        Optional<NucleusKitService> optSrv = NucleusAPI.getKitService();
        if (!optSrv.isPresent()) {
            throw new InternalServerErrorException("Nuclues kit service not available");
        }

        NucleusKitService srv = optSrv.get();

        Optional<CachedKit> optRes = WebAPIAPI.runOnMain(() -> {
            Optional<Kit> optKit = srv.getKit(name);
            if (!optKit.isPresent()) {
                throw new NotFoundException("Kit with name " + name + " not found");
            }

            srv.removeKit(name);
            return new CachedKit(optKit.get());
        });

        return optRes.orElse(null);
    }
}
