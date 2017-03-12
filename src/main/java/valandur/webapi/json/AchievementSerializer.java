package valandur.webapi.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.statistic.achievement.Achievement;

import java.io.IOException;

public class AchievementSerializer extends StdSerializer<Achievement> {

    public AchievementSerializer() {
        this(null);
    }

    public AchievementSerializer(Class<Achievement> t) {
        super(t);
    }

    @Override
    public void serialize(Achievement value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("id", value.getId());
        gen.writeStringField("name", value.getName());
        gen.writeStringField("type", value.getType().getName());
        gen.writeStringField("description", value.getDescription().get());
        gen.writeEndObject();
    }
}
