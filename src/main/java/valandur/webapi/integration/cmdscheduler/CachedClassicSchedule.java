package valandur.webapi.integration.cmdscheduler;

import com.mcsimonflash.sponge.cmdscheduler.schedule.ClassicSchedule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ClassicSchedule")
public class CachedClassicSchedule extends CachedSchedule<ClassicSchedule> {

    private String type;
    @ApiModelProperty("The type of schedule.")
    public String getType() {
        return "CLASSIC";
    }

    private boolean delayInTicks;
    @ApiModelProperty("True if the delay is in ticks, false if it is in milliseconds.")
    public boolean isDelayInTicks() {
        return delayInTicks;
    }

    private boolean intervalInTicks;
    @ApiModelProperty("True if the interval is in ticks, false if it is in milliseconds.")
    public boolean isIntervalInTicks() {
        return intervalInTicks;
    }


    public CachedClassicSchedule(ClassicSchedule value) {
        super(value);

        this.delayInTicks = value.isDelayTicks();
        this.intervalInTicks = value.isIntervalTicks();
    }
}
