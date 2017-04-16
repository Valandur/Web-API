package valandur.webapi.json.serializers.player;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.data.manipulator.mutable.entity.ExperienceHolderData;

import java.io.IOException;

public class ExperienceHolderDataSerializer extends StdSerializer<ExperienceHolderData> {

    public ExperienceHolderDataSerializer() {
        this(null);
    }

    public ExperienceHolderDataSerializer(Class<ExperienceHolderData> t) {
        super(t);
    }

    @Override
    public void serialize(ExperienceHolderData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("level", value.level().get());
        gen.writeNumberField("experience", value.experienceSinceLevel().get());
        gen.writeNumberField("totalExperience", value.totalExperience().get());
        gen.writeEndObject();
    }
}
