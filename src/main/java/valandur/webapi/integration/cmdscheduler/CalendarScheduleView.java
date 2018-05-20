package valandur.webapi.integration.cmdscheduler;

import com.mcsimonflash.sponge.cmdscheduler.schedule.CalendarSchedule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.serialize.BaseView;

import java.util.Date;

@ApiModel(value = "CalendarSchedule")
public class CalendarScheduleView extends BaseView<CalendarSchedule> {

    @ApiModelProperty("The type of schedule.")
    public String getType() {
        return "CALENDAR";
    }

    @ApiModelProperty("The date at which this command will be executed.")
    public Date getDate() {
        return value.getCalendar().getTime();
    }

    @ApiModelProperty("The interval at which the command will be executed, after this first execution at the date.")
    public long getInterval() {
        return value.getInterval();
    }


    public CalendarScheduleView(CalendarSchedule value) {
        super(value);
    }
}
