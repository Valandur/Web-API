package valandur.webapi.json.serializer.item;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.effect.potion.PotionEffect;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class PotionEffectSerializer extends WebAPISerializer<PotionEffect> {
    @Override
    public void serialize(PotionEffect value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "id", value.getType().getId());
        writeField(provider, "name", value.getType().getTranslation().get());
        writeField(provider, "amplifier", value.getAmplifier());
        writeField(provider, "duration", value.getDuration());
        gen.writeEndObject();
    }
}
