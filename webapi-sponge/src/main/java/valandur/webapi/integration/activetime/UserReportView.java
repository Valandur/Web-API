package valandur.webapi.integration.activetime;

import com.mcsimonflash.sponge.activetime.objects.TimeHolder;
import com.mcsimonflash.sponge.activetime.objects.UserReport;
import io.swagger.annotations.ApiModel;
import valandur.webapi.serialize.BaseView;

import java.time.LocalDate;
import java.util.UUID;

@ApiModel("UserReport")
public class UserReportView extends BaseView<UserReport> {

    public UUID getUUID() {
        return value.uuid;
    }
    public String getName() {
        return value.name;
    }

    public LocalDate getFrom() {
        return value.from;
    }
    public LocalDate getTo() {
        return value.to;
    }

    public TimeHolder getTotal() {
        return value.total;
    }
    public TimeHolder getDailyAverage() {
        return value.dailyAverage;
    }
    public TimeHolder getMonthlyAverage() {
        return value.monthlyAverage;
    }
    public TimeHolder getWeeklyAverage() {
        return value.weeklyAverage;
    }


    public UserReportView(UserReport value) {
        super(value);
    }
}
