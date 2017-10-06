package valandur.webapi.json.view.item;

import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import valandur.webapi.api.json.BaseView;

public class PotionEffectView extends BaseView<PotionEffect> {

    public PotionEffectType type;
    public int amplifier;
    public int duration;


    public PotionEffectView(PotionEffect value) {
        super(value);

        this.type = value.getType();
        this.amplifier = value.getAmplifier();
        this.duration = value.getDuration();
    }
}
