package valandur.webapi.serialize.view.item;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import valandur.webapi.api.serialize.BaseView;

@ApiModel("Enchantment")
public class EnchantmentView extends BaseView<Enchantment> {

    @ApiModelProperty("The unique id of the enchantment type")
    public String getId() {
        return value.getType().getId();
    }

    @ApiModelProperty("The name of the enchantment type")
    public String getName() {
        return value.getType().getTranslation().get();
    }

    @ApiModelProperty("The level of the enchantment")
    public int getLevel() {
        return value.getLevel();
    }


    public EnchantmentView(Enchantment value) {
        super(value);
    }
}
