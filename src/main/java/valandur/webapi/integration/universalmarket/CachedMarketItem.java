package valandur.webapi.integration.universalmarket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xwaffle.universalmarket.market.MarketItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.item.CachedItemStack;
import valandur.webapi.cache.player.CachedPlayer;
import valandur.webapi.util.Constants;

@ApiModel("UniversalMarketItem")
public class CachedMarketItem extends CachedObject<MarketItem> {

    private CachedItemStack display;
    @ApiModelProperty(value = "The ItemStack displayed to the users", required = true)
    public CachedItemStack getDisplay() {
        return display;
    }

    private CachedItemStack item;
    @ApiModelProperty(value = "The ItemStack that is being sold", required = true)
    public CachedItemStack getItem() {
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

    private CachedPlayer owner;
    @ApiModelProperty(value = "The owner that submitted this offer", required = true)
    public CachedPlayer getOwner() {
        return owner;
    }


    public CachedMarketItem(MarketItem value) {
        super(value);

        this.display = new CachedItemStack(value.getDisplay());
        this.item = new CachedItemStack(value.getItem());
        this.expires = value.getExpireTime() / 1000;
        this.price = value.getPrice();
        this.owner = WebAPI.getCacheService().getPlayer(value.getOwnerUUID()).orElse(null);
    }

    @Override
    @JsonIgnore(false)
    public String getLink() {
        return Constants.BASE_PATH + "/universal-market/item/" + item.getType().getId();
    }
}
