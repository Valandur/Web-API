package valandur.webapi.servlet;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import valandur.webapi.cache.permission.CachedSubject;
import valandur.webapi.cache.permission.CachedSubjectCollection;
import valandur.webapi.servlet.base.BaseServlet;
import valandur.webapi.servlet.base.ExplicitDetails;
import valandur.webapi.servlet.base.Permission;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Path("permission")
@Api(tags = { "Permission" }, value = "Manage permissions on the server")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class PermissionServlet extends BaseServlet {

    @GET
    @Path("/collection")
    @Permission({ "collection", "list" })
    @ExplicitDetails
    @ApiOperation(
            value = "List collections",
            notes = "Gets a list of all the subject collections, for example groups, users, etc.")
    public Set<CachedSubjectCollection> listCollections() {
        PermissionService srv = getPermissionService();

        try {
            Set<SubjectCollection> colls = new HashSet<>();
            for (String id : srv.getAllIdentifiers().get()) {
                colls.add(srv.loadCollection(id).get());
            }
            return colls.stream().map(CachedSubjectCollection::new).collect(Collectors.toSet());
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
    public CachedSubjectCollection getCollection(@PathParam("id") String id) throws NotFoundException {
        PermissionService srv = getPermissionService();

        try {
            if (!srv.hasCollection(id).get())
                throw new NotFoundException("Collection with id " + id + " could not be found");

            return new CachedSubjectCollection(srv.loadCollection(id).get());
        } catch (InterruptedException e) {
            throw new ClientErrorException(e.getMessage(), Response.Status.REQUEST_TIMEOUT);
        } catch (ExecutionException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/collection/{id}/subject")
    @Permission({ "collection", "subject", "list" })
    @ExplicitDetails
    @ApiOperation(
            value = "List subjects",
            notes = "List all subjects belonging to a certain collection")
    public Set<CachedSubject> listSubjects(@PathParam("id") String id) throws NotFoundException {
        PermissionService srv = getPermissionService();

        try {
            if (!srv.hasCollection(id).get())
                throw new NotFoundException("Collection with id " + id + " could not be found");

            Set<Subject> subjects = new HashSet<>();
            SubjectCollection coll = srv.loadCollection(id).get();
            for (String subId : coll.getAllIdentifiers().get()) {
                subjects.add(coll.loadSubject(subId).get());
            }
            return subjects.stream().map(CachedSubject::new).collect(Collectors.toSet());
        } catch (InterruptedException e) {
            throw new ClientErrorException(e.getMessage(), Response.Status.REQUEST_TIMEOUT);
        } catch (ExecutionException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/collection/{id}/subject/{subId}")
    @Permission({ "collection", "subject", "one" })
    @ApiOperation(
            value = "Get subject",
            notes = "Gets one specific subject belonging to a certain collection")
    public CachedSubject getSubject(@PathParam("id") String id, @PathParam("subId") String subId)
            throws NotFoundException {
        PermissionService srv = getPermissionService();

        try {
            if (!srv.hasCollection(id).get())
                throw new NotFoundException("Collection with id " + id + " could not be found");

            SubjectCollection coll = srv.loadCollection(id).get();
            if (!coll.hasSubject(subId).get())
                throw new NotFoundException("Subject with id " + id + " could not be found");

            return new CachedSubject(coll.loadSubject(subId).get());
        } catch (InterruptedException e) {
            throw new ClientErrorException(e.getMessage(), Response.Status.REQUEST_TIMEOUT);
        } catch (ExecutionException e) {
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
