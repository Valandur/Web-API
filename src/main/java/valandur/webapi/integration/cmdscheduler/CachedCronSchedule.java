package valandur.webapi.integration.cmdscheduler;

import com.mcsimonflash.sponge.cmdscheduler.schedule.CronSchedule;
import com.mcsimonflash.sponge.cmdscheduler.schedule.Units;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ApiModel(value = "CronSchedule")
public class CachedCronSchedule extends CachedSchedule<CronSchedule> {

    @ApiModelProperty("The type of schedule.")
    public String getType() {
        return "CRON";
    }

    private Map<Units, Set<Integer>> units;
    @ApiModelProperty("The cron times for each time unit at which the schedule will be executed")
    public Map<Units, Set<Integer>> getUnits() {
        return units;
    }


    public CachedCronSchedule(CronSchedule value) {
        super(value);

        this.units = new HashMap<>();
        for (Units unit : Units.values()) {
            this.units.put(unit, value.getUnit(unit));
        }
    }
}
