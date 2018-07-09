package valandur.webapi.integration.cmdscheduler;

import com.mcsimonflash.sponge.cmdscheduler.schedule.CalendarSchedule;
import com.mcsimonflash.sponge.cmdscheduler.schedule.ClassicSchedule;
import com.mcsimonflash.sponge.cmdscheduler.schedule.CronSchedule;
import com.mcsimonflash.sponge.cmdscheduler.schedule.Schedule;
import com.mcsimonflash.sponge.cmdscheduler.task.CommandTask;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.cache.CachedObject;

@ApiModel("CommandTask")
public class CachedCommandTask extends CachedObject<CommandTask> {

    private String name;
    @ApiModelProperty("The name of this task")
    public String getName() {
        return name;
    }

    private String command;
    @ApiModelProperty("The command that is executed")
    public String getCommand() {
        return command;
    }

    private CachedSchedule schedule;
    @ApiModelProperty("The command that is executed")
    public CachedSchedule getSchedule() {
        return schedule;
    }


    public CachedCommandTask(CommandTask value) {
        super(value);

        this.name = value.getName();
        this.command = value.getCommand();

        Schedule schedule = value.getTask().getSchedule();
        if (schedule instanceof ClassicSchedule) {
            this.schedule = new CachedClassicSchedule((ClassicSchedule) schedule);
        } else if (schedule instanceof CalendarSchedule) {
            this.schedule = new CachedCalendarSchedule((CalendarSchedule) schedule);
        } else if (schedule instanceof CronSchedule) {
            this.schedule = new CachedCronSchedule((CronSchedule) schedule);
        }
    }
}
