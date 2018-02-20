package valandur.webapi.serialize.view.item;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import valandur.webapi.api.serialize.BaseView;

@ApiModel("PotionEffect")
public class PotionEffectView extends BaseView<PotionEffect> {

    @ApiModelProperty(value = "The type of effect this potion represents", required = true)
    public PotionEffectType type;

    @ApiModelProperty(value = "The aplifier of this potion (I, II, III, IV, V, ...)", required = true)
    public int amplifier;

    @ApiModelProperty(value = "The duration this potion lasts for", required = true)
    public int duration;


    public PotionEffectView(PotionEffect value) {
        super(value);

        this.type = value.getType();
        this.amplifier = value.getAmplifier();
        this.duration = value.getDuration();
    }
}
