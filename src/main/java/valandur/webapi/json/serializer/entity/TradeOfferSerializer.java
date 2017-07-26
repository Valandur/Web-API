package valandur.webapi.json.serializer.entity;

import org.spongepowered.api.item.merchant.TradeOffer;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class TradeOfferSerializer extends WebAPIBaseSerializer<TradeOffer> {
    @Override
    public void serialize(TradeOffer value) throws IOException {
        writeStartObject();
        writeField("hasExpired", value.hasExpired());
        writeField("grantsExp", value.doesGrantExperience());
        writeField("uses", value.getUses());
        writeField("maxUses", value.getUses());
        writeField("firstBuyingItem", value.getFirstBuyingItem());
        writeField("secondBuyingItem", value.getSecondBuyingItem().orElse(null));
        writeField("sellingItem", value.getSellingItem());
        writeEndObject();
    }
}
