package valandur.webapi.serialize.view.item;

import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.item.Enchantment;
import valandur.webapi.serialize.BaseView;

public class ItemEnchantmentView extends BaseView<ItemEnchantment> {

    public Enchantment enchantment;
    public int level;


    public ItemEnchantmentView(ItemEnchantment value) {
        super(value);

        this.enchantment = value.getEnchantment();
        this.level = value.getLevel();
    }
}
