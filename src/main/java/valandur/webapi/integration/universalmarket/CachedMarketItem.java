package valandur.webapi.integration.universalmarket;

import com.xwaffle.universalmarket.market.MarketItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.api.WebAPIAPI;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.player.ICachedPlayer;
import valandur.webapi.util.Constants;

@ApiModel("UniversalMarketItem")
public class CachedMarketItem extends CachedObject<MarketItem> {

    private ItemStack display;
    @ApiModelProperty(value = "The ItemStack displayed to the users", required = true)
    public ItemStack getDisplay() {
        return display;
    }

    private ItemStack item;
    @ApiModelProperty(value = "The ItemStack that is being sold", required = true)
    public ItemStack getItem() {
        return item;
    }

    private long expires;
    @ApiModelProperty(value = "The unix timestamp (in seconds) at which this offer will expire", required = true)
    public long getExpires() {
        return expires;
    }

    private double price;
    @ApiModelProperty(value = "The price this item is being sold for", required = true)
    public double getPrice() {
        return price;
    }

    private ICachedPlayer owner;
    @ApiModelProperty(value = "The owner that submitted this offer", required = true)
    public ICachedPlayer getOwner() {
        return owner;
    }


    public CachedMarketItem(MarketItem value) {
        super(value);

        this.display = value.getDisplay().copy();
        this.item = value.getItem().copy();
        this.expires = value.getExpireTime() / 1000;
        this.price = value.getPrice();
        this.owner = WebAPIAPI.getCacheService().flatMap(srv -> srv.getPlayer(value.getOwnerUUID())).orElse(null);
    }

    @Override
    public String getLink() {
        return Constants.BASE_PATH + "/universal-market/item/" + item.getItem().getId();
    }
}
