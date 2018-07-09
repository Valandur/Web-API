package valandur.webapi.integration.activetime;

import com.mcsimonflash.sponge.activetime.objects.ServerReport;
import io.swagger.annotations.ApiModel;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedLocalDate;

@ApiModel("ServerReport")
public class CachedServerReport extends CachedObject<ServerReport> {

    public CachedLocalDate from;
    public CachedLocalDate to;

    public CachedTimeHolder total;
    public CachedTimeHolder dailyAverage;
    public CachedTimeHolder weeklyAverage;
    public CachedTimeHolder monthlyAverage;


    public CachedServerReport(ServerReport value) {
        super(value);

        this.from = new CachedLocalDate(value.from);
        this.to = new CachedLocalDate(value.to);

        this.total = new CachedTimeHolder(value.total);
        this.dailyAverage = new CachedTimeHolder(value.dailyAverage);
        this.weeklyAverage = new CachedTimeHolder(value.weeklyAverage);
        this.monthlyAverage = new CachedTimeHolder(value.monthlyAverage);
    }
}
