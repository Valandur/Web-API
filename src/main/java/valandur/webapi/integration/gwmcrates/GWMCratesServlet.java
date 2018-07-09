package valandur.webapi.integration.gwmcrates;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.caze.Case;
import org.gwmdevelopments.sponge_plugin.crates.drop.Drop;
import org.gwmdevelopments.sponge_plugin.crates.key.Key;
import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import org.gwmdevelopments.sponge_plugin.crates.preview.Preview;
import valandur.webapi.WebAPI;
import valandur.webapi.serialize.SerializeService;
import valandur.webapi.servlet.base.BaseServlet;
import valandur.webapi.servlet.base.ExplicitDetails;
import valandur.webapi.servlet.base.Permission;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

@Path("gwm-crates")
@Api(
        tags = { "Integration", "GWMCrates" },
        value = "Create, edit and delete the crate types on your server, without having to restart it.")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class GWMCratesServlet extends BaseServlet {

    public static void onRegister() {
        SerializeService srv = WebAPI.getSerializeService();
        srv.registerCache(Manager.class, CachedManager.class);
        srv.registerCache(Drop.class, CachedDrop.class);
        srv.registerCache(Key.class, CachedKey.class);
        srv.registerCache(Case.class, CachedCase.class);
        srv.registerCache(Preview.class, CachedPreview.class);
    }

    @GET
    @Path("/manager")
    @Permission({ "manager", "list" })
    @ExplicitDetails
    @ApiOperation(
            value = "List managers",
            notes = "Get a list of all the managers on the server.")
    public List<CachedManager> listCrates() {
        return WebAPI.runOnMain(() -> GWMCrates.getInstance().getCreatedManagers()
                .stream()
                .map(CachedManager::new)
                .collect(Collectors.toList()));
    }
}
