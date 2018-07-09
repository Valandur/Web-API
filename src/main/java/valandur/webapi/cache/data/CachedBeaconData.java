package valandur.webapi.cache.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.tileentity.BeaconData;
import org.spongepowered.api.effect.potion.PotionEffectType;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;

@ApiModel("BeaconData")
public class CachedBeaconData extends CachedObject<BeaconData> {

    @ApiModelProperty("The primary effect of the beacon")
    public CachedCatalogType<PotionEffectType> primary;

    @ApiModelProperty("The secondary effect of the beacon")
    public CachedCatalogType<PotionEffectType> secondary;


    public CachedBeaconData(BeaconData value) {
        super(value);

        this.primary = value.primaryEffect().get().map(CachedCatalogType::new).orElse(null);
        this.secondary = value.secondaryEffect().get().map(CachedCatalogType::new).orElse(null);
    }
}
