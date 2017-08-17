package valandur.webapi.integration.nucleus;

import io.github.nucleuspowered.nucleus.api.nucleusdata.Home;
import org.spongepowered.api.util.Tristate;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class HomeSerializer extends WebAPIBaseSerializer<Home> {

    @Override
    protected void serialize(Home value) throws IOException {
        writeStartObject();
        writeField("name", value.getName());
        writeField("user", value.getUser(), Tristate.FALSE);
        writeField("location", value.getLocation().orElse(null), Tristate.FALSE);
        writeEndObject();
    }
}
