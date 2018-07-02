package valandur.webapi.integration.gmwcrates;

import com.codehusky.huskycrates.HuskyCrates;
import com.codehusky.huskycrates.crate.VirtualCrate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import valandur.webapi.WebAPI;
import valandur.webapi.integration.huskycrates.CachedVirtualCrate;
import valandur.webapi.servlet.base.BaseServlet;
import valandur.webapi.servlet.base.Permission;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Path("gmw-crates")
@Api(
        tags = { "GMWCrates" },
        value = "Create, edit and delete the crate types on your server, without having to restart it.")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class GMWCratesServlet extends BaseServlet {

    @GET
    @Path("/crate")
    @Permission({ "crate", "list" })
    @ApiOperation(
            value = "List crates",
            notes = "Get a list of all the crates on the server.")
    public Collection<CachedVirtualCrate> listCrates() {
        return WebAPI.runOnMain(() -> {
            GWMCrates.
        });
    }
}
