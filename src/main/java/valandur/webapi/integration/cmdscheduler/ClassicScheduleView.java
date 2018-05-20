package valandur.webapi.integration.cmdscheduler;

import com.mcsimonflash.sponge.cmdscheduler.schedule.ClassicSchedule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.serialize.BaseView;

@ApiModel(value = "ClassicSchedule")
public class ClassicScheduleView extends BaseView<ClassicSchedule> {

    @ApiModelProperty("The type of schedule.")
    public String getType() {
        return "CLASSIC";
    }

    @ApiModelProperty("The delay until the first execution of this command.")
    public long getDelay() {
        return value.getDelay();
    }

    @ApiModelProperty("True if the delay is in ticks, false if it is in milliseconds.")
    public boolean isDelayInTicks() {
        return value.isDelayTicks();
    }

    @ApiModelProperty("The interval between executions of this command.")
    public long getInterval() {
        return value.getInterval();
    }

    @ApiModelProperty("True if the interval is in ticks, false if it is in milliseconds.")
    public boolean isIntervalInTicks() {
        return value.isIntervalTicks();
    }


    public ClassicScheduleView(ClassicSchedule value) {
        super(value);
    }
}
