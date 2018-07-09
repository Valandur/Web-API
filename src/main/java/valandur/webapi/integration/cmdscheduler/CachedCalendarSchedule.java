package valandur.webapi.integration.cmdscheduler;

import com.mcsimonflash.sponge.cmdscheduler.schedule.CalendarSchedule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel(value = "CalendarSchedule")
public class CachedCalendarSchedule extends CachedSchedule<CalendarSchedule> {

    @ApiModelProperty("The type of schedule.")
    public String getType() {
        return "CALENDAR";
    }

    private Date date;
    @ApiModelProperty("The date at which this command will be executed.")
    public Date getDate() {
        return date;
    }


    public CachedCalendarSchedule(CalendarSchedule value) {
        super(value);

        this.date = value.getCalendar().getTime();
    }
}
