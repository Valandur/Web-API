package valandur.webapi.json.serializers.player;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.data.manipulator.mutable.entity.AchievementData;
import org.spongepowered.api.statistic.achievement.Achievement;
import valandur.webapi.json.JsonConverter;

import java.io.IOException;

public class AchievementDataSerializer extends StdSerializer<AchievementData> {

    public AchievementDataSerializer() {
        this(null);
    }

    public AchievementDataSerializer(Class<AchievementData> t) {
        super(t);
    }

    @Override
    public void serialize(AchievementData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartArray();

        for (Achievement a : value.achievements().get()) {
            gen.writeObject(JsonConverter.toJson(a));
        }

        gen.writeEndArray();
    }
}
