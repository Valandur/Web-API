package valandur.webapi.integration.cmdscheduler;

import com.mcsimonflash.sponge.cmdscheduler.schedule.Schedule;
import com.mcsimonflash.sponge.cmdscheduler.task.CommandTask;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.serialize.BaseView;

import java.util.List;

@ApiModel("CommandTask")
public class CommandTaskView extends BaseView<CommandTask> {

    @ApiModelProperty("The name of this task")
    public String getName() {
        return value.getName();
    }

    @ApiModelProperty("The list of commands that are executed")
    public List<String> getCommands() {
        return value.getCommands();
    }

    @ApiModelProperty("The command that is executed")
    public Schedule getSchedule() {
        return value.getTask().getSchedule();
    }


    public CommandTaskView(CommandTask value) {
        super(value);
    }
}
