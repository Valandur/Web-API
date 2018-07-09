package valandur.webapi.integration.activetime;

import com.mcsimonflash.sponge.activetime.objects.UserReport;
import io.swagger.annotations.ApiModel;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedLocalDate;

import java.util.UUID;

@ApiModel("UserReport")
public class CachedUserReport extends CachedObject<UserReport> {

    public UUID uuid;
    public String name;

    public CachedLocalDate from;
    public CachedLocalDate to;

    public CachedTimeHolder total;
    public CachedTimeHolder monthlyAverage;
    public CachedTimeHolder dailyAverage;
    public CachedTimeHolder weeklyAverage;


    public CachedUserReport(UserReport value) {
        super(value);

        this.uuid = value.uuid;
        this.name = value.name;

        this.from = new CachedLocalDate(value.from);
        this.to = new CachedLocalDate(value.to);

        this.total = new CachedTimeHolder(value.total);
        this.dailyAverage = new CachedTimeHolder(value.dailyAverage);
        this.weeklyAverage = new CachedTimeHolder(value.weeklyAverage);
        this.monthlyAverage = new CachedTimeHolder(value.monthlyAverage);
    }
}
