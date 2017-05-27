package valandur.webapi.json.serializer.event;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.util.Tristate;
import valandur.webapi.json.serializer.WebAPISerializer;
import valandur.webapi.misc.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CauseSerializer extends WebAPISerializer<Cause> {

    private List<String> blockedCauseClasses;


    public CauseSerializer() {
        super();

        List<String> classes = new ArrayList<>();

        classes.add("net.minecraft.server");
        classes.add("valandur.webapi");

        blockedCauseClasses = classes;
    }

    @Override
    public void serialize(Cause value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        for (Map.Entry<String, Object> entry : value.getNamedCauses().entrySet()) {
            String key = Util.lowerFirst(entry.getKey());
            if (blockedCauseClasses.stream().anyMatch(c -> entry.getValue().getClass().getName().startsWith(c)))
                writeField(provider, key, entry.getValue().getClass().getName());
            else
                writeField(provider, key, entry.getValue(), Tristate.FALSE);
        }

        gen.writeEndObject();
    }
}
