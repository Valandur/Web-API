package valandur.webapi.servlet;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Permission;
import valandur.webapi.handler.NotImplementedException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("recipe")
@Api(tags = { "Recipe" }, value = "List all recipes available on the server")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class RecipeServlet extends BaseServlet {

    @GET
    @Permission("list")
    @ApiOperation("List recipes")
    public Object getRecipes() {
        throw new NotImplementedException();
    }
}
