package valandur.webapi.integration.nucleus;

import io.github.nucleuspowered.nucleus.api.nucleusdata.NamedLocation;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class NamedLocationSerializer extends WebAPIBaseSerializer<NamedLocation> {

    @Override
    protected void serialize(NamedLocation value) throws IOException {
        writeStartObject();
        writeField("name", value.getName());
        writeField("location", value.getLocation().orElse(null));
        writeEndObject();
    }
}
