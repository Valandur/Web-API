package valandur.webapi.json.serializers.item;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class PotionEffectDataSerializer extends WebAPISerializer<PotionEffectData> {
    @Override
    public void serialize(PotionEffectData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartArray();
        for (PotionEffect effect : value.asList()) {
            gen.writeObject(effect);
        }
        gen.writeEndArray();
    }
}
