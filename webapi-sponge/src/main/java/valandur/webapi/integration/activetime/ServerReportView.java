package valandur.webapi.integration.activetime;

import com.mcsimonflash.sponge.activetime.objects.ServerReport;
import com.mcsimonflash.sponge.activetime.objects.TimeHolder;
import io.swagger.annotations.ApiModel;
import valandur.webapi.serialize.BaseView;

import java.time.LocalDate;

@ApiModel("ServerReport")
public class ServerReportView extends BaseView<ServerReport> {

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


    public ServerReportView(ServerReport value) {
        super(value);
    }
}
