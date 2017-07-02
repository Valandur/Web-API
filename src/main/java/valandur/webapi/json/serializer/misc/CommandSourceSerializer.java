package valandur.webapi.json.serializer.misc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.command.CommandSource;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class CommandSourceSerializer extends WebAPIBaseSerializer<CommandSource> {
    @Override
    public void serialize(CommandSource value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        writeValue(provider, value.getName());
    }
}
