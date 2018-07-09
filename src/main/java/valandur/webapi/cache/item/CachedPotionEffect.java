package valandur.webapi.cache.item;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;

@ApiModel("PotionEffect")
public class CachedPotionEffect extends CachedObject<PotionEffect> {

    @ApiModelProperty(value = "The type of effect this potion represents", required = true)
    public CachedCatalogType<PotionEffectType> type;

    @ApiModelProperty(value = "The aplifier of this potion (I, II, III, IV, V, ...)", required = true)
    public int amplifier;

    @ApiModelProperty(value = "The duration this potion lasts for", required = true)
    public int duration;


    public CachedPotionEffect(PotionEffect value) {
        super(value);

        this.type = new CachedCatalogType<>(value.getType());
        this.amplifier = value.getAmplifier();
        this.duration = value.getDuration();
    }
}
