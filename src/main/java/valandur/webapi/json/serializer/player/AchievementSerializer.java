package valandur.webapi.json.serializer.player;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.statistic.achievement.Achievement;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class AchievementSerializer extends WebAPIBaseSerializer<Achievement> {
    @Override
    public void serialize(Achievement value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "id", value.getId());
        writeField(provider, "name", value.getName());
        writeField(provider, "class", value.getClass().getName());
        writeField(provider, "description", value.getDescription().get());
        gen.writeEndObject();
    }
}
