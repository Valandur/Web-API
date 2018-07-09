package valandur.webapi.integration.nucleus;

import com.flowpowered.math.vector.Vector3d;
import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import io.github.nucleuspowered.nucleus.api.nucleusdata.Kit;
import io.github.nucleuspowered.nucleus.api.nucleusdata.MailMessage;
import io.github.nucleuspowered.nucleus.api.nucleusdata.NamedLocation;
import io.github.nucleuspowered.nucleus.api.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.spongepowered.api.entity.living.player.User;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.item.CachedItemStackSnapshot;
import valandur.webapi.cache.player.CachedPlayer;
import valandur.webapi.exceptions.NotImplementedException;
import valandur.webapi.serialize.SerializeService;
import valandur.webapi.servlet.base.BaseServlet;
import valandur.webapi.servlet.base.Permission;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("nucleus")
@Api(tags = { "Integration", "Nucleus" }, value = "Manage your kits and jails")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class NucleusServlet extends BaseServlet {

    public static void onRegister() {
        SerializeService srv = WebAPI.getSerializeService();
        srv.registerCache(NamedLocation.class, CachedNamedLocation.class);
        srv.registerCache(MailMessage.class, CachedMailMessage.class);
        srv.registerCache(Kit.class, CachedKit.class);
    }


    // Jails
    @GET
    @Path("/jail")
    @Permission({ "jail", "list" })
    @ApiOperation(value = "List jails", notes = "Get a list of all the jails on the server.")
    public Collection<CachedNamedLocation> listJails() {
        Optional<NucleusJailService> optSrv = NucleusAPI.getJailService();
        if (!optSrv.isPresent()) {
            throw new InternalServerErrorException("Nuclues jail service not available");
        }

        NucleusJailService srv = optSrv.get();

        return WebAPI.runOnMain(
                () -> srv.getJails().values().stream()
                        .map(CachedNamedLocation::new)
                        .collect(Collectors.toList())
        );
    }

    @GET
    @Path("/jail/{name}")
    @Permission({ "jail", "one" })
    @ApiOperation(value = "Get a jail", notes = "Get detailed information about a jail.")
    public CachedNamedLocation getJail(@PathParam("name") String name)
            throws NotFoundException {
        Optional<NucleusJailService> optSrv = NucleusAPI.getJailService();
        if (!optSrv.isPresent()) {
            throw new InternalServerErrorException("Nuclues jail service not available");
        }

        NucleusJailService srv = optSrv.get();

        return WebAPI.runOnMain(() -> {
            Optional<NamedLocation> optJail = srv.getJail(name);
            if (!optJail.isPresent()) {
                throw new NotFoundException("Jail with name " + name + " not found");
            }

            return new CachedNamedLocation(optJail.get());
        });
    }

    @POST
    @Path("/jail")
    @Permission({ "jail", "create" })
    @ApiOperation(value = "Create a jail", response = CachedNamedLocation.class, notes = "Creates a new jail.")
    public Response createJail(CachedNamedLocation req)
            throws BadRequestException, URISyntaxException {

        if (req == null) {
            throw new BadRequestException("Request body is required");
        }

        Optional<NucleusJailService> optSrv = NucleusAPI.getJailService();
        if (!optSrv.isPresent()) {
            throw new InternalServerErrorException("Nuclues jail service not available");
        }

        NucleusJailService srv = optSrv.get();

        if (req.getLocation() == null) {
            throw new BadRequestException("A location is required");
        }

        CachedNamedLocation jail = WebAPI.runOnMain(() -> {
            Vector3d rot = req.getRotation() == null ? Vector3d.FORWARD : req.getRotation().getLive();
            srv.setJail(req.getName(), req.getLocation().getLive(), rot);
            Optional<NamedLocation> optJail = srv.getJail(req.getName());
            if (!optJail.isPresent()) {
                throw new InternalServerErrorException("Could not get jail after creating it");
            }
            return new CachedNamedLocation(optJail.get());
        });

        return Response.created(new URI(null, null, jail.getLink(), null)).entity(jail).build();
    }

    @PUT
    @Path("/jail/{name}")
    @Permission({ "jail", "modify" })
    @ApiOperation(value = "Modify a jail", notes = "Modify an existing jail.")
    public CachedNamedLocation modifyJail()
            throws NotImplementedException {
        throw new NotImplementedException();
    }

    @DELETE
    @Path("/jail/{name}")
    @Permission({ "jail", "delete" })
    @ApiOperation(value = "Delete a jail", notes = "Delete an existing jail.")
    public CachedNamedLocation deleteJail(@PathParam("name") String name)
            throws NotFoundException {
        Optional<NucleusJailService> optSrv = NucleusAPI.getJailService();
        if (!optSrv.isPresent()) {
            throw new InternalServerErrorException("Nuclues jail service not available");
        }

        NucleusJailService srv = optSrv.get();

        return WebAPI.runOnMain(() -> {
            Optional<NamedLocation> optJail = srv.getJail(name);
            if (!optJail.isPresent()) {
                throw new NotFoundException("Jail with name " + name + " not found");
            }

            srv.removeJail(name);

            return new CachedNamedLocation(optJail.get());
        });
    }


    // Kits
    @GET
    @Path("/kit")
    @Permission({ "kit", "list" })
    @ApiOperation(value = "List kits", notes = "Get a list of all the kits on the server.")
    public Collection<CachedKit> listKits() {
        Optional<NucleusKitService> optSrv = NucleusAPI.getKitService();
        if (!optSrv.isPresent()) {
            throw new InternalServerErrorException("Nuclues kit service not available");
        }

        NucleusKitService srv = optSrv.get();

        return WebAPI.runOnMain(
                () -> srv.getKitNames().stream()
                        .map(name -> srv.getKit(name).map(CachedKit::new).orElse(null))
                        .collect(Collectors.toList())
        );
    }

    @GET
    @Path("/kit/{name}")
    @Permission({ "kit", "one" })
    @ApiOperation(value = "Get a kit", notes = "Get detailed information about a kit.")
    public CachedKit getKit(@PathParam("name") String name)
            throws NotFoundException {
        Optional<NucleusKitService> optSrv = NucleusAPI.getKitService();
        if (!optSrv.isPresent()) {
            throw new InternalServerErrorException("Nuclues kit service not available");
        }

        NucleusKitService srv = optSrv.get();

        return WebAPI.runOnMain(() -> {
            Optional<Kit> optKit = srv.getKit(name);
            if (!optKit.isPresent()) {
                throw new NotFoundException("Kit with name " + name + " not found");
            }

            return new CachedKit(optKit.get());
        });
    }

    @POST
    @Path("/kit")
    @Permission({ "kit", "create" })
    @ApiOperation(value = "Create a kit", response = CachedKit.class, notes = "Creates a new kit.")
    public Response createKit(CachedKit req)
            throws BadRequestException, URISyntaxException {

        if (req == null) {
            throw new BadRequestException("Request body is required");
        }

        Optional<NucleusKitService> optSrv = NucleusAPI.getKitService();
        if (!optSrv.isPresent()) {
            throw new InternalServerErrorException("Nuclues kit service not available");
        }

        NucleusKitService srv = optSrv.get();

        if (req.getName().isEmpty()) {
            throw new BadRequestException("Invalid kit name");
        }

        CachedKit resKit = WebAPI.runOnMain(() -> {
            Kit kit = srv.createKit(req.getName());
            kit.setCost(req.getCost());
            kit.setCooldown(Duration.ofMillis(req.getCooldown()));
            if (req.getStacks() != null) {
                try {
                    kit.setStacks(req.getStacks().stream()
                            .map(CachedItemStackSnapshot::getLive)
                            .collect(Collectors.toList()));
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

        return Response.created(new URI(null, null, resKit.getLink(), null)).entity(resKit).build();
    }

    @PUT
    @Path("/kit/{name}")
    @Permission({ "kit", "modify" })
    @ApiOperation(value = "Modify a kit", notes = "Modify an existing kit.")
    public CachedKit modifyKit(@PathParam("name") String name, CachedKit req)
            throws NotFoundException, BadRequestException {

        if (req == null) {
            throw new BadRequestException("Request body is required");
        }

        Optional<NucleusKitService> optSrv = NucleusAPI.getKitService();
        if (!optSrv.isPresent()) {
            throw new InternalServerErrorException("Nuclues kit service not available");
        }

        NucleusKitService srv = optSrv.get();

        return WebAPI.runOnMain(() -> {
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
                    kit.setStacks(req.getStacks().stream()
                            .map(CachedItemStackSnapshot::getLive)
                            .collect(Collectors.toList()));
                } catch (Exception e) {
                    throw new BadRequestException("Could not process item stack: " + e.getMessage());
                }
            }

            srv.saveKit(kit);

            return new CachedKit(kit);
        });
    }

    @DELETE
    @Path("/kit/{name}")
    @Permission({ "kit", "delete" })
    @ApiOperation(value = "Delete a kit", notes = "Delete an existing kit.")
    public CachedKit deleteKit(@PathParam("name") String name)
            throws NotFoundException {
        Optional<NucleusKitService> optSrv = NucleusAPI.getKitService();
        if (!optSrv.isPresent()) {
            throw new InternalServerErrorException("Nuclues kit service not available");
        }

        NucleusKitService srv = optSrv.get();

        return WebAPI.runOnMain(() -> {
            Optional<Kit> optKit = srv.getKit(name);
            if (!optKit.isPresent()) {
                throw new NotFoundException("Kit with name " + name + " not found");
            }

            srv.removeKit(name);
            return new CachedKit(optKit.get());
        });
    }


    // Home
    @GET
    @Path("/home/{player}")
    @Permission({ "home", "list" })
    @ApiOperation(value = "List homes", notes = "Get a list of all the homes of a player.")
    public Collection<CachedNamedLocation> listHomes(
            @PathParam("player") @ApiParam("The uuid of the player") CachedPlayer player)
            throws NotFoundException {

        Optional<NucleusHomeService> optSrv = NucleusAPI.getHomeService();
        if (!optSrv.isPresent()) {
            throw new InternalServerErrorException("Nuclues home service not available");
        }

        NucleusHomeService srv = optSrv.get();

        return WebAPI.runOnMain(
                () -> srv.getHomes(player.getUUID()).stream()
                        .map(CachedNamedLocation::new)
                        .collect(Collectors.toList())
        );
    }


    // Mail
    @GET
    @Path("/mail/{player}")
    @Permission({ "mail", "list" })
    @ApiOperation(value = "List mail", notes = "Get a list of all mail messages of a player.")
    public Collection<CachedMailMessage> listMail(
            @PathParam("player") @ApiParam("The uuid of the player") CachedPlayer player)
            throws NotFoundException {

        Optional<NucleusMailService> optSrv = NucleusAPI.getMailService();
        if (!optSrv.isPresent()) {
            throw new InternalServerErrorException("Nuclues mail service not available");
        }

        NucleusMailService srv = optSrv.get();

        Optional<User> optUser = player.getUser();
        if (!optUser.isPresent()) {
            throw new InternalServerErrorException("Could not get user");
        }

        return WebAPI.runOnMain(
                () -> srv.getMail(optUser.get(), mailMessage -> true).stream()
                        .map(CachedMailMessage::new)
                        .collect(Collectors.toList())
        );
    }


    // Modules
    @GET
    @Path("/module")
    @Permission({ "module", "list" })
    @ApiOperation(value = "List modules", notes = "Get a list of loaded Nucleus modules.")
    public Collection<String> listModules() {

        NucleusModuleService srv = NucleusAPI.getModuleService();
        return WebAPI.runOnMain(srv::getModulesToLoad);
    }
}
