package valandur.webapi.integration.villagershops.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.dosmike.sponge.vshop.InvPrep;
import de.dosmike.sponge.vshop.NPCguard;
import de.dosmike.sponge.vshop.StockItem;
import de.dosmike.sponge.vshop.VillagerShops;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.service.economy.Currency;
import valandur.webapi.Constants;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;

import javax.ws.rs.BadRequestException;
import java.util.Optional;
import java.util.UUID;

@ApiModel("VillagerShopsStockItem")
public class CachedStockItem extends CachedObject<StockItem> {

    Integer id;

    @ApiModelProperty(value = "The index of this item withing the shops inventory")
    public int getId() {
        return id;
    }

    String shopId;

    @ApiModelProperty(value = "The shop uuid offering this item listing", required = true)
    public UUID getShopId() {
        return UUID.fromString(shopId);
    }

    Double buyPrice;

    @JsonProperty
    @ApiModelProperty(value = "The amount of money this stack consts to buy as player", required = true)
    public Double getBuyPrice() {
        return buyPrice;
    }

    Double sellPrice;

    @JsonProperty
    @ApiModelProperty(value = "The amount of money this stack earns the player when selling", required = true)
    public Double getSellPrice() {
        return sellPrice;
    }

    CachedCatalogType<Currency> currency;

    @JsonProperty
    @ApiModelProperty(value = "The currency for this item listing", required = true)
    public CachedCatalogType<Currency> getCurrency() {
        return currency;
    }

    Integer stock;

    @JsonProperty
    @ApiModelProperty(value = "If this shop has a limited stock, returns how many items are stocked, otherwise returns items stack size", required = true)
    public Integer getStock() {
        return stock;
    }

    int maxStock;

    @JsonProperty
    @ApiModelProperty(value = "If this shop has a limited stock, returns how many of these items can be stocked, 0 is unlimited", required = true)
    public int getMaxStock() {
        return maxStock;
    }

    Boolean hasStock;

    @JsonProperty
    @ApiModelProperty(value = "Returns wether this shop has a limited stock", required = true)
    public Boolean hasStock() {
        return hasStock;
    }

    ItemStackSnapshot item;

    @JsonProperty
    @ApiModelProperty(value = "The raw ItemStack data for this shop listing", required = true)
    public ItemStackSnapshot getItem() {
        return item;
    }


    public CachedStockItem() {
        super(null);
    }

    public CachedStockItem(StockItem item, int id, UUID shopID) {
        super(item);

        this.id = id;
        this.shopId = shopID == null ? null : shopID.toString();
        this.buyPrice = item.getBuyPrice();
        this.sellPrice = item.getSellPrice();
        this.currency = new CachedCatalogType<>(item.getCurrency());
        this.stock = item.getStocked();
        this.maxStock = item.getMaxStock();
        this.hasStock = this.maxStock > 0;
        this.item = item.getItem().createSnapshot();
    }

    @Override
    @ApiModelProperty(required = false)
    public String getLink() {
        return Constants.BASE_PATH + "/vshop/shop/" + shopId + "/item/" + id;
    }

    @Override
    public Optional<StockItem> getLive() {
        if (id < 0 || shopId == null)
            return Optional.empty();
        Optional<NPCguard> g = VillagerShops.getNPCfromShopUUID(getShopId());
        if (!g.isPresent())
            return Optional.empty();
        InvPrep inv = g.get().getPreparator();
        if (inv.size() <= id)
            return Optional.empty();
        return Optional.of(inv.getItem(id));
    }

    public void validate() throws BadRequestException {
        if (!hasStock && maxStock > 0)
            throw new BadRequestException("Can't set max stock for items without hasstock flag");
        if (maxStock < 0)
            throw new BadRequestException("Max stock can't be negative");
        if (sellPrice == null && buyPrice == null)
            throw new BadRequestException("Buy price and sell price can't both be omitted!");
        if (sellPrice != null && sellPrice < 0)
            throw new BadRequestException("Sell price can't be negative");
        if (buyPrice != null && buyPrice < 0)
            throw new BadRequestException("Buy price can't be negative");
        if (item == null)
            throw new BadRequestException("Missing item snapshot");
    }
}
