package valandur.webapi.serialize.view.item;

import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import valandur.webapi.api.serialize.BaseView;

public class EnchantmentView extends BaseView<Enchantment> {

    public EnchantmentType enchantment;
    public int level;


    public EnchantmentView(Enchantment value) {
        super(value);

        this.enchantment = value.getType();
        this.level = value.getLevel();
    }
}
