package valandur.webapi.servlet;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import valandur.webapi.WebAPI;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Permission;
import valandur.webapi.security.SecurityContext;
import valandur.webapi.server.ServerProperty;
import valandur.webapi.server.ServerService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

@Path("server")
@Api(tags = { "Server" }, value = "Prodives information about the server, such as server propertiers")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class ServerServlet extends BaseServlet {

    @GET
    @Path("/properties")
    @Permission({ "properties", "list" })
    @ApiOperation(
            value = "List server properties",
            notes = "Get the main server properties (server.properties file).")
    public Collection<ServerProperty> listProperties() {
        ServerService srv = WebAPI.getServerService();
        return srv.getProperties();
    }

    @PUT
    @Path("/properties")
    @Permission({ "properties", "modify" })
    @Permission(value = { "properties", "modify", "[property]" }, autoCheck = false)
    @ApiOperation(
            value = "Modify server properties",
            notes = "Modify settings in the server.properties file. **Note that these settings don't take effect " +
                    "until you restart the server.")
    public Collection<ServerProperty> modifyProperties(
            final Map<String, String> properties,
            @Context HttpServletRequest request)
            throws BadRequestException {

        if (properties == null) {
            throw new BadRequestException("Request body is required");
        }

        SecurityContext context = (SecurityContext)request.getAttribute("security");

        ServerService srv = WebAPI.getServerService();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (!context.hasPerms(entry.getKey())) {
                throw new ForbiddenException("You do not have permission to change the " + entry.getKey() + " setting");
            }
            srv.setProperty(entry.getKey(), entry.getValue());
        }

        try {
            srv.saveProperties();
        } catch (IOException e) {
            WebAPI.sentryCapture(e);
            throw new InternalServerErrorException(e.getMessage());
        }

        return srv.getProperties();
    }
}
