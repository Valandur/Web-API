package valandur.webapi.api.cache.player;

import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.api.cache.entity.ICachedEntity;

public interface ICachedPlayer extends ICachedEntity {

    String getName();

    String getAddress();

    int getLatency();

    ItemStack getHelmet();

    ItemStack getChestplate();

    ItemStack getLeggings();

    ItemStack getBoots();
}
