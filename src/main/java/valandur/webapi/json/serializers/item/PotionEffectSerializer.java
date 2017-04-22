package valandur.webapi.json.serializers.item;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.effect.potion.PotionEffect;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class PotionEffectSerializer extends WebAPISerializer<PotionEffect> {
    @Override
    public void serialize(PotionEffect value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("type", value.getType().getId());
        gen.writeNumberField("amplifier", value.getAmplifier());
        gen.writeNumberField("duration", value.getDuration());
        gen.writeEndObject();
    }
}
