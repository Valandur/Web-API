package valandur.webapi.integration.cmdscheduler;

import com.mcsimonflash.sponge.cmdscheduler.schedule.Schedule;
import com.mcsimonflash.sponge.cmdscheduler.task.CommandTask;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.serialize.BaseView;

@ApiModel("CommandTask")
public class CommandTaskView extends BaseView<CommandTask> {

    @ApiModelProperty("The name of this task")
    public String getName() {
        return value.getName();
    }

    @ApiModelProperty("The command that is executed")
    public String getCommand() {
        return value.getCommand();
    }

    @ApiModelProperty("The command that is executed")
    public Schedule getSchedule() {
        return value.getTask().getSchedule();
    }


    public CommandTaskView(CommandTask value) {
        super(value);
    }
}
