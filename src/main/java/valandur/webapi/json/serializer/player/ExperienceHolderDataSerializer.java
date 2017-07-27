package valandur.webapi.json.serializer.player;

import org.spongepowered.api.data.manipulator.mutable.entity.ExperienceHolderData;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class ExperienceHolderDataSerializer extends WebAPIBaseSerializer<ExperienceHolderData> {
    @Override
    public void serialize(ExperienceHolderData value) throws IOException {
        writeStartObject();
        writeField("level", value.level().get());
        writeField("experience", value.experienceSinceLevel().get());
        writeField("totalExperience", value.totalExperience().get());
        writeEndObject();
    }
}
