package valandur.webapi.serialize.view.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.tileentity.BeaconData;
import org.spongepowered.api.effect.potion.PotionEffectType;
import valandur.webapi.serialize.BaseView;

@ApiModel("BeaconData")
public class BeaconDataView extends BaseView<BeaconData> {

    @ApiModelProperty("The primary effect of the beacon")
    public PotionEffectType primary;

    @ApiModelProperty("The secondary effect of the beacon")
    public PotionEffectType secondary;


    public BeaconDataView(BeaconData value) {
        super(value);

        this.primary = value.primaryEffect().get().orElse(null);
        this.secondary = value.secondaryEffect().get().orElse(null);
    }
}
