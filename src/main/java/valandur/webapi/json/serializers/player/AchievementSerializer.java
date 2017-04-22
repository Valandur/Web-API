package valandur.webapi.json.serializers.player;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.statistic.achievement.Achievement;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class AchievementSerializer extends WebAPISerializer<Achievement> {
    @Override
    public void serialize(Achievement value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("id", value.getId());
        gen.writeStringField("name", value.getName());
        gen.writeStringField("class", value.getClass().getName());
        gen.writeStringField("description", value.getDescription().get());
        gen.writeEndObject();
    }
}
