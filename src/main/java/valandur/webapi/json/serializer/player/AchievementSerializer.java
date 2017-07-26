package valandur.webapi.json.serializer.player;

import org.spongepowered.api.statistic.achievement.Achievement;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class AchievementSerializer extends WebAPIBaseSerializer<Achievement> {

    @Override
    public void serialize(Achievement value) throws IOException {
        writeStartObject();
        writeField("id", value.getId());
        writeField("name", value.getName());
        writeField("class", value.getClass().getName());
        writeField("description", value.getDescription().get());
        writeEndObject();
    }
}
