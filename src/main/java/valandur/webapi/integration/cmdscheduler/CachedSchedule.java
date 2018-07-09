package valandur.webapi.integration.cmdscheduler;

import com.mcsimonflash.sponge.cmdscheduler.schedule.Schedule;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.cache.CachedObject;

public class CachedSchedule<T extends Schedule> extends CachedObject<T> {

    protected long delay;
    @ApiModelProperty("The delay until the first execution of this command.")
    public long getDelay() {
        return delay;
    }

    protected long interval;
    @ApiModelProperty("The interval between executions of this command.")
    public long getInterval() {
        return interval;
    }


    public CachedSchedule(T value) {
        super(value);

        this.delay = value.getDelay();
        this.interval = value.getInterval();
    }
}
