package valandur.webapi.cache.item;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;

@ApiModel("Enchantment")
public class CachedEnchantment extends CachedObject<Enchantment> {

    private CachedCatalogType<EnchantmentType> type;
    @ApiModelProperty("The type of the enchantment")
    public CachedCatalogType<EnchantmentType> getType() {
        return type;
    }

    private int level;
    @ApiModelProperty("The level of the enchantment")
    public int getLevel() {
        return level;
    }


    public CachedEnchantment(Enchantment value) {
        super(value);

        this.type = new CachedCatalogType<>(value.getType());
        this.level = value.getLevel();
    }
}
