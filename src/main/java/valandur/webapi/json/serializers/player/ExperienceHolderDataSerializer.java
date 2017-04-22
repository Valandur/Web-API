package valandur.webapi.json.serializers.player;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.data.manipulator.mutable.entity.ExperienceHolderData;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class ExperienceHolderDataSerializer extends WebAPISerializer<ExperienceHolderData> {
    @Override
    public void serialize(ExperienceHolderData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("level", value.level().get());
        gen.writeNumberField("experience", value.experienceSinceLevel().get());
        gen.writeNumberField("totalExperience", value.totalExperience().get());
        gen.writeEndObject();
    }
}
