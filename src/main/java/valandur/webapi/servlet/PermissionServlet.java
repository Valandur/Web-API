package valandur.webapi.servlet;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.ExplicitDetails;
import valandur.webapi.api.servlet.Permission;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
    public Set<SubjectCollection> listCollections() {
        PermissionService srv = getPermissionService();

        Set<SubjectCollection> colls = new HashSet<>();
        for (Map.Entry<String, SubjectCollection> entry : srv.getKnownSubjects().entrySet()) {
            colls.add(entry.getValue());
        }
        return colls;
    }

    @GET
    @Path("/collection/{id}")
    @Permission({ "collection", "one" })
    @ApiOperation(
            value = "Get collection",
            notes = "Gets a specific subject collection")
    public SubjectCollection getCollection(@PathParam("id") String id) throws NotFoundException {
        PermissionService srv = getPermissionService();
        return srv.getSubjects(id);
    }

    @GET
    @Path("/collection/{id}/subject")
    @Permission({ "collection", "subject", "list" })
    @ExplicitDetails
    @ApiOperation(
            value = "List subjects",
            notes = "List all subjects belonging to a certain collection")
    public Set<Subject> listSubjects(@PathParam("id") String id) throws NotFoundException {
        PermissionService srv = getPermissionService();

        SubjectCollection coll = srv.getSubjects(id);
        if (coll == null)
            throw new NotFoundException("Collection with id " + id + " could not be found");

        Set<Subject> subjects = new HashSet<>();
        for (Subject sub : coll.getAllSubjects()) {
            subjects.add(sub);
        }
        return subjects;
    }

    @GET
    @Path("/collection/{id}/subject/{subId}")
    @Permission({ "collection", "subject", "list" })
    @ApiOperation(
            value = "Get subject",
            notes = "Gets one specific subject belonging to a certain collection")
    public Subject getSubject(@PathParam("id") String id, @PathParam("subId") String subId) throws NotFoundException {
        PermissionService srv = getPermissionService();
        SubjectCollection coll = srv.getSubjects(id);
        if (coll == null)
            throw new NotFoundException("Collection with id " + id + " could not be found");

        Subject subj = coll.get(subId);
        if (subj == null)
            throw new NotFoundException("Subject with id " + id + " could not be found");

        return subj;
    }


    private PermissionService getPermissionService() {
        Optional<PermissionService> optSrv = Sponge.getServiceManager().provide(PermissionService.class);
        if (!optSrv.isPresent())
            throw new NotFoundException("Permission service was not found");
        return optSrv.get();
    }
}
