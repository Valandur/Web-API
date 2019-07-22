package valandur.webapi.integration.cmdscheduler;

import com.mcsimonflash.sponge.cmdscheduler.schedule.CronSchedule;
import com.mcsimonflash.sponge.cmdscheduler.schedule.Units;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.serialize.BaseView;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ApiModel(value = "CronSchedule")
public class CronScheduleView extends BaseView<CronSchedule> {

    @ApiModelProperty("The type of schedule.")
    public String getType() {
        return "CRON";
    }

    @ApiModelProperty("The cron times for each time unit at which the schedule will be executed")
    public Map<Units, Set<Integer>> getUnits() {
        Map<Units, Set<Integer>> map = new HashMap<>();
        for (Units unit : Units.values()) {
            map.put(unit, value.getUnit(unit));
        }
        return map;
    }


    public CronScheduleView(CronSchedule value) {
        super(value);
    }
}
