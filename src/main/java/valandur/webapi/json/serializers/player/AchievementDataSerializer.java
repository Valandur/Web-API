package valandur.webapi.json.serializers.player;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.data.manipulator.mutable.entity.AchievementData;
import org.spongepowered.api.statistic.achievement.Achievement;
import valandur.webapi.json.JsonConverter;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class AchievementDataSerializer extends WebAPISerializer<AchievementData> {
    @Override
    public void serialize(AchievementData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartArray();

        for (Achievement a : value.achievements().get()) {
            gen.writeObject(JsonConverter.toJson(a));
        }

        gen.writeEndArray();
    }
}
