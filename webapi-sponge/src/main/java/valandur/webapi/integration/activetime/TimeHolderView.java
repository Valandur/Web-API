package valandur.webapi.integration.activetime;

import com.mcsimonflash.sponge.activetime.objects.TimeHolder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.serialize.BaseView;

@ApiModel("TimeHolder")
public class TimeHolderView extends BaseView<TimeHolder> {

    @ApiModelProperty("The amount of active time spent")
    public int getActiveTime() {
        return value.getActiveTime();
    }

    @ApiModelProperty("The amount of time spent afk (only works if Nucleus is present)")
    public int getAfkTime() {
        return value.getAfkTime();
    }


    public TimeHolderView(TimeHolder value) {
        super(value);
    }
}
