package valandur.webapi.json.serializer.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.item.merchant.TradeOffer;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class TradeOfferSerializer extends WebAPIBaseSerializer<TradeOffer> {
    @Override
    public void serialize(TradeOffer value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "hasExpired", value.hasExpired());
        writeField(provider, "grantsExp", value.doesGrantExperience());
        writeField(provider, "uses", value.getUses());
        writeField(provider, "maxUses", value.getUses());
        writeField(provider, "firstBuyingItem", value.getFirstBuyingItem());
        writeField(provider, "secondBuyingItem", value.getSecondBuyingItem().orElse(null));
        writeField(provider, "sellingItem", value.getSellingItem());
        gen.writeEndObject();
    }
}
