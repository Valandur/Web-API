package valandur.webapi.json.serializers.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.data.manipulator.mutable.entity.TradeOfferData;
import org.spongepowered.api.item.merchant.TradeOffer;

import java.io.IOException;

public class TradeOfferDataSerializer extends StdSerializer<TradeOfferData> {

    public TradeOfferDataSerializer() {
        this(null);
    }

    public TradeOfferDataSerializer(Class<TradeOfferData> t) {
        super(t);
    }

    @Override
    public void serialize(TradeOfferData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartArray();
        for (TradeOffer offer : value.asList()) {
            gen.writeObject(offer);
        }
        gen.writeEndArray();
    }
}
