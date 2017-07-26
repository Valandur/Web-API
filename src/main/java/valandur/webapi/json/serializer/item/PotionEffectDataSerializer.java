package valandur.webapi.json.serializer.item;

import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class PotionEffectDataSerializer extends WebAPIBaseSerializer<PotionEffectData> {
    @Override
    public void serialize(PotionEffectData value) throws IOException {
        writeStartArray();
        for (PotionEffect effect : value.asList()) {
            writeValue(effect);
        }
        writeEndArray();
    }
}
