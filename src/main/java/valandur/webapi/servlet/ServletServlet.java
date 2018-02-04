/*package valandur.webapi.servlet;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Permission;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.Map;

@Path("servlet")
@Api(value = "servlet", tags = { "Servlet" })
public class ServletServlet extends BaseServlet {

    @GET
    @Permission("list")
    @ApiOperation("List servlets")
    public Map<String, IServletService.ServletInfo> getServlets() {
        return servletService.getLoadedServlets();
    }
}
*/