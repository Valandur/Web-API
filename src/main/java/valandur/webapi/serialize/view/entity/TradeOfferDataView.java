package valandur.webapi.serialize.view.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.TradeOfferData;
import org.spongepowered.api.item.merchant.TradeOffer;
import valandur.webapi.api.serialize.BaseView;

import java.util.List;

public class TradeOfferDataView extends BaseView<TradeOfferData> {

    @JsonValue
    public List<TradeOffer> offers;


    public TradeOfferDataView(TradeOfferData value) {
        super(value);

        this.offers = value.tradeOffers().get();
    }
}
