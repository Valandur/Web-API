package valandur.webapi.serialize.view.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.merchant.TradeOffer;
import valandur.webapi.serialize.BaseView;

@ApiModel("TradeOffer")
public class TradeOfferView extends BaseView<TradeOffer> {

    @ApiModelProperty(value = "True if this offer is expired, false otherwise", required = true)
    public boolean expired;

    @ApiModelProperty(value = "True if this trade grants experience when completed, false otherwise", required = true)
    public boolean grantsExp;

    @ApiModelProperty(value = "The amount of times this trade has been used", required = true)
    public int uses;

    @ApiModelProperty(value = "The maximum amount of times that this trade can be used", required = true)
    public int maxUses;

    @ApiModelProperty(value = "The first item that is required for the trade", required = true)
    public ItemStackSnapshot firstBuyingItem;

    @ApiModelProperty("The second item that is required for the trade")
    public ItemStackSnapshot secondBuyingItem;

    @ApiModelProperty(value = "The item that is received when trading", required = true)
    public ItemStackSnapshot sellingItem;


    public TradeOfferView(TradeOffer value) {
        super(value);

        this.expired = value.hasExpired();
        this.grantsExp = value.doesGrantExperience();
        this.uses = value.getUses();
        this.maxUses = value.getUses();
        this.firstBuyingItem = value.getFirstBuyingItem();
        this.secondBuyingItem = value.getSecondBuyingItem().orElse(null);
        this.sellingItem = value.getSellingItem();
    }
}
