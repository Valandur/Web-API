package valandur.webapi.servlet;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Permission;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Path("permission")
@Api(tags = { "Permission" }, value = "Manage permissions on the server")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class PermissionServlet extends BaseServlet {

    @GET
    @Path("/collection")
    @Permission({ "collection", "list" })
    @ApiOperation(
            value = "List collections",
            notes = "Gets a list of all the subject collections, for example groups, users, etc.")
    public Set<String> listCollections() {
        PermissionService srv = getPermissionService();

        try {
            return srv.getAllIdentifiers().get();
        } catch (InterruptedException e) {
            throw new ClientErrorException(e.getMessage(), Response.Status.REQUEST_TIMEOUT);
        } catch (ExecutionException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/collection/{id}")
    @Permission({ "collection", "one" })
    @ApiOperation(
            value = "Get collection",
            notes = "Gets a specific subject collection")
    public SubjectCollection getCollection(@PathParam("id") String id) {
        PermissionService srv = getPermissionService();

        try {
            if (!srv.hasCollection(id).get())
                throw new NotFoundException("Subject collection with id " + id + " could not be found");

            return srv.loadCollection(id).get();
        } catch (InterruptedException e) {
            throw new ClientErrorException(e.getMessage(), Response.Status.REQUEST_TIMEOUT);
        } catch (ExecutionException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/user")
    @Permission({ "user", "list" })
    @ApiOperation(
            value = "List users",
            notes = "List all user identifiers")
    public Set<String> listUsers() {
        PermissionService srv = getPermissionService();

        try {
            return srv.getUserSubjects().getAllIdentifiers().get();
        } catch (InterruptedException e) {
            throw new ClientErrorException(e.getMessage(), Response.Status.REQUEST_TIMEOUT);
        } catch (ExecutionException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/user/{id}")
    @Permission({ "user", "one" })
    @ApiOperation(
            value = "Get a user",
            notes = "Gets a single subject from the user subject collection")
    public Subject getUser(@PathParam("id") String id) {
        PermissionService srv = getPermissionService();

        try {
            if (!srv.getUserSubjects().hasSubject(id).get())
                throw new NotFoundException("User subject with id " + id + " could not be found");

            return srv.getUserSubjects().loadSubject(id).get();
        } catch (InterruptedException e) {
            throw new ClientErrorException(e.getMessage(), Response.Status.REQUEST_TIMEOUT);
        } catch (ExecutionException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/group")
    @Permission({ "group", "list" })
    @ApiOperation(
            value = "List groups",
            notes = "List all group identifiers")
    public Set<String> listGroups() {
        PermissionService srv = getPermissionService();

        try {
            return srv.getGroupSubjects().getAllIdentifiers().get();
        } catch (InterruptedException e) {
            throw new ClientErrorException(e.getMessage(), Response.Status.REQUEST_TIMEOUT);
        } catch (ExecutionException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/group/{id}")
    @Permission({ "group", "one" })
    @ApiOperation(
            value = "Get a group",
            notes = "Gets a single subject from the group subject collection")
    public Subject getGroup(@PathParam("id") String id) {
        PermissionService srv = getPermissionService();

        try {
            if (!srv.getGroupSubjects().hasSubject(id).get())
                throw new NotFoundException("Group subject with id " + id + " could not be found");

            return srv.getGroupSubjects().loadSubject(id).get();
        } catch (InterruptedException e) {
            throw new ClientErrorException(e.getMessage(), Response.Status.REQUEST_TIMEOUT);
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }


    private PermissionService getPermissionService() {
        Optional<PermissionService> optSrv = Sponge.getServiceManager().provide(PermissionService.class);
        if (!optSrv.isPresent())
            throw new NotFoundException("Permission service was not found");
        return optSrv.get();
    }
}
