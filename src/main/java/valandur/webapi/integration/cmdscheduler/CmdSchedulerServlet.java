package valandur.webapi.integration.cmdscheduler;

import com.mcsimonflash.sponge.cmdscheduler.internal.Config;
import com.mcsimonflash.sponge.cmdscheduler.schedule.CalendarSchedule;
import com.mcsimonflash.sponge.cmdscheduler.schedule.ClassicSchedule;
import com.mcsimonflash.sponge.cmdscheduler.schedule.CronSchedule;
import com.mcsimonflash.sponge.cmdscheduler.task.CommandTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import valandur.webapi.WebAPI;
import valandur.webapi.serialize.SerializeService;
import valandur.webapi.servlet.base.BaseServlet;
import valandur.webapi.servlet.base.Permission;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Path("cmd-scheduler")
@Api(tags = { "CmdScheduler" }, value = "Automatic Command Scheduler")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class CmdSchedulerServlet extends BaseServlet {

    public static void onRegister() {
        SerializeService srv = WebAPI.getSerializeService();
        srv.registerView(CommandTask.class, CommandTaskView.class);
        srv.registerView(CronSchedule.class, CronScheduleView.class);
        srv.registerView(ClassicSchedule.class, ClassicScheduleView.class);
        srv.registerView(CalendarSchedule.class, CalendarScheduleView.class);
    }

    @GET
    @Permission({ "list" })
    @ApiOperation(
            value = "List tasks",
            notes = "Gets a list of all the registered tasks.")
    public Collection<CommandTask> listTasks() {
        return WebAPI.runOnMain(() -> Config.tasks.values());
    }

    @DELETE
    @Path("/{name}")
    @Permission({ "delete" })
    @ApiOperation(
            value = "Delete a task",
            notes = "Deletes a task.")
    public CommandTask deleteTask(
            @PathParam("name") @ApiParam("The name of the task") String name)
            throws NotFoundException {

        return WebAPI.runOnMain(() -> {
            CommandTask task = Config.tasks.get(name);
            if (task == null) {
                throw new NotFoundException("Task '" + name + "' not found");
            }

            Config.tasks.remove(name);
            task.getTask().stop(WebAPI.getContainer());
            return task;
        });
    }
}
