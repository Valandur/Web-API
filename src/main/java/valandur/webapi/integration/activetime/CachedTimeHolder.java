package valandur.webapi.integration.activetime;

import com.mcsimonflash.sponge.activetime.objects.TimeHolder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.cache.CachedObject;

@ApiModel("TimeHolder")
public class CachedTimeHolder extends CachedObject<TimeHolder> {

    private int activeTime;
    @ApiModelProperty("The amount of active time spent")
    public int getActiveTime() {
        return activeTime;
    }

    private int afkTime;
    @ApiModelProperty("The amount of time spent afk (only works if Nucleus is present)")
    public int getAfkTime() {
        return afkTime;
    }


    public CachedTimeHolder(TimeHolder value) {
        super(value);

        this.activeTime = value.getActiveTime();
        this.afkTime = value.getAfkTime();
    }
}
