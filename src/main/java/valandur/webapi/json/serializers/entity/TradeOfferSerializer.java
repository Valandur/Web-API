package valandur.webapi.json.serializers.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.item.merchant.TradeOffer;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class TradeOfferSerializer extends WebAPISerializer<TradeOffer> {
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
