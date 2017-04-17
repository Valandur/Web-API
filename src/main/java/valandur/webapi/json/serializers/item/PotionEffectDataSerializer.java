package valandur.webapi.json.serializers.item;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;

import java.io.IOException;

public class PotionEffectDataSerializer extends StdSerializer<PotionEffectData> {

    public PotionEffectDataSerializer() {
        this(null);
    }

    public PotionEffectDataSerializer(Class<PotionEffectData> t) {
        super(t);
    }

    @Override
    public void serialize(PotionEffectData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartArray();
        for (PotionEffect effect : value.asList()) {
            gen.writeObject(effect);
        }
        gen.writeEndArray();
    }
}
