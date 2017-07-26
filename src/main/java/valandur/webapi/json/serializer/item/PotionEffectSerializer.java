package valandur.webapi.json.serializer.item;

import org.spongepowered.api.effect.potion.PotionEffect;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class PotionEffectSerializer extends WebAPIBaseSerializer<PotionEffect> {
    @Override
    public void serialize(PotionEffect value) throws IOException {
        writeStartObject();
        writeField("id", value.getType().getId());
        writeField("name", value.getType().getTranslation().get());
        writeField("amplifier", value.getAmplifier());
        writeField("duration", value.getDuration());
        writeEndObject();
    }
}
