package valandur.webapi.json.serializer.player;

import org.spongepowered.api.data.manipulator.mutable.entity.AchievementData;
import org.spongepowered.api.statistic.achievement.Achievement;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class AchievementDataSerializer extends WebAPIBaseSerializer<AchievementData> {

    @Override
    public void serialize(AchievementData value) throws IOException {
        writeStartArray();

        for (Achievement a : value.achievements().get()) {
            writeValue(a);
        }

        writeEndArray();
    }
}
