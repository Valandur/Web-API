package valandur.webapi.integration.activetime;

import com.mcsimonflash.sponge.activetime.api.ActiveTimeAPI;
import com.mcsimonflash.sponge.activetime.objects.ServerReport;
import com.mcsimonflash.sponge.activetime.objects.TimeHolder;
import com.mcsimonflash.sponge.activetime.objects.UserReport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.spongepowered.api.entity.living.player.User;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.player.CachedPlayer;
import valandur.webapi.serialize.SerializeService;
import valandur.webapi.servlet.base.BaseServlet;
import valandur.webapi.servlet.base.Permission;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Path("active-time")
@Api(tags = { "Active Time" }, value = "Keep track of the play time of players on your server.")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class ActiveTimeServlet extends BaseServlet {

    public static void onRegister() {
        SerializeService srv = WebAPI.getSerializeService();
        srv.registerView(ServerReport.class, ServerReportView.class);
        srv.registerView(TimeHolder.class, TimeHolderView.class);
        srv.registerView(UserReport.class, UserReportView.class);
    }

    @GET
    @Permission({ "report", "server" })
    @ApiOperation(
            value = "Server report",
            notes = "Generates a report for the whole server from week ago until now")
    public ServerReport getServerReport() {
        try {
            return ActiveTimeAPI.getServerReport(LocalDate.now().minusWeeks(1), LocalDate.now()).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/user/{uuid}")
    @Permission({ "report", "user" })
    @ApiOperation(
            value = "User report",
            notes = "Generates a report for a specific user from a week ago until now")
    public UserReport getUserReport(@PathParam("uuid") CachedPlayer player) {
        try {
            CompletableFuture<UserReport> report = WebAPI.runOnMain(() -> {
                Optional<User> optUser = player.getUser();
                if (!optUser.isPresent())
                    throw new InternalServerErrorException("Could not get live user");
                return ActiveTimeAPI.getUserReport(optUser.get(), LocalDate.now().minusWeeks(1), LocalDate.now());
            });
            return report.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}
