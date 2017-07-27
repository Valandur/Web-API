package valandur.webapi.json.serializer.entity;

import org.spongepowered.api.data.manipulator.mutable.entity.TradeOfferData;
import org.spongepowered.api.item.merchant.TradeOffer;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class TradeOfferDataSerializer extends WebAPIBaseSerializer<TradeOfferData> {
    @Override
    public void serialize(TradeOfferData value) throws IOException {
        writeStartArray();
        for (TradeOffer offer : value.asList()) {
            writeValue(offer);
        }
        writeEndArray();
    }
}
