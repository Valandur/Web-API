package valandur.webapi.json.serializer.player;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.data.manipulator.mutable.entity.AchievementData;
import org.spongepowered.api.statistic.achievement.Achievement;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class AchievementDataSerializer extends WebAPISerializer<AchievementData> {
    @Override
    public void serialize(AchievementData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartArray();

        for (Achievement a : value.achievements().get()) {
            writeValue(provider, a);
        }

        gen.writeEndArray();
    }
}
