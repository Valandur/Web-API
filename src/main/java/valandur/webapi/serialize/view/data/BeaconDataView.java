package valandur.webapi.serialize.view.data;

import org.spongepowered.api.data.manipulator.mutable.tileentity.BeaconData;
import org.spongepowered.api.effect.potion.PotionEffectType;
import valandur.webapi.api.serialize.BaseView;

public class BeaconDataView extends BaseView<BeaconData> {

    public PotionEffectType primary;
    public PotionEffectType secondary;


    public BeaconDataView(BeaconData value) {
        super(value);

        this.primary = value.primaryEffect().get().orElse(null);
        this.secondary = value.secondaryEffect().get().orElse(null);
    }
}
