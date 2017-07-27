package valandur.webapi.json.serializer.event;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.util.Tristate;
import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CauseSerializer extends WebAPIBaseSerializer<Cause> {

    private List<String> blockedCauseClasses;


    public CauseSerializer() {
        super();

        List<String> classes = new ArrayList<>();

        classes.add("net.minecraft.server");
        classes.add("valandur.webapi");

        blockedCauseClasses = classes;
    }

    @Override
    public void serialize(Cause value) throws IOException {
        writeStartObject();

        for (Map.Entry<String, Object> entry : value.getNamedCauses().entrySet()) {
            String key = Util.lowerFirst(entry.getKey());
            if (blockedCauseClasses.stream().anyMatch(c -> entry.getValue().getClass().getName().startsWith(c)))
                writeField(key, entry.getValue().getClass().getName());
            else
                writeField(key, entry.getValue(), Tristate.FALSE);
        }

        writeEndObject();
    }
}
