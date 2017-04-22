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
        gen.writeBooleanField("hasExpired", value.hasExpired());
        gen.writeBooleanField("grantsExp", value.doesGrantExperience());
        gen.writeNumberField("uses", value.getUses());
        gen.writeNumberField("maxUses", value.getUses());
        gen.writeObjectField("firstBuyingItem", value.getFirstBuyingItem());
        gen.writeObjectField("secondBuyingItem", value.getSecondBuyingItem().orElse(null));
        gen.writeObjectField("sellingItem", value.getSellingItem());
        gen.writeEndObject();
    }
}
