package valandur.webapi.serialize.view.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.entity.DespawnDelayData;
import valandur.webapi.serialize.BaseView;

@ApiModel("DespawnDelayData")
public class DespawnDelayDataView extends BaseView<DespawnDelayData> {

    @ApiModelProperty(value = "The amount of time until this entity despawns", required = true)
    public int delay;

    @ApiModelProperty(value = "True if this entity never despawns, false otherwise", required = true)
    public boolean infinite;


    public DespawnDelayDataView(DespawnDelayData value) {
        super(value);

        this.delay = value.delay().get();
        this.infinite = value.infinite().get();
    }
}
