package valandur.webapi.serialize.view.entity;

import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.merchant.TradeOffer;
import valandur.webapi.api.serialize.BaseView;

public class TradeOfferView extends BaseView<TradeOffer> {

    public boolean expired;
    public boolean grantsExp;
    public int uses;
    public int maxUses;
    public ItemStackSnapshot firstBuyingItem;
    public ItemStackSnapshot secondBuyingItem;
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
