package valandur.webapi.integration.universalmarket;

import com.xwaffle.universalmarket.market.MarketItem;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.player.ICachedPlayer;

public class CachedMarketItem extends CachedObject<MarketItem> {

    private ItemStack display;
    public ItemStack getDisplay() {
        return display;
    }

    private ItemStack item;
    public ItemStack getItem() {
        return item;
    }

    private long expires;
    public long getExpires() {
        return expires;
    }

    private double price;
    public double getPrice() {
        return price;
    }

    private ICachedPlayer owner;
    public ICachedPlayer getOwner() {
        return owner;
    }


    public CachedMarketItem(MarketItem value) {
        super(value);

        this.display = value.getDisplay().copy();
        this.item = value.getItem().copy();
        this.expires = value.getExpireTime() / 1000;
        this.price = value.getPrice();
        this.owner = WebAPI.getCacheService().getPlayer(value.getOwnerUUID()).orElse(null);
    }
}
