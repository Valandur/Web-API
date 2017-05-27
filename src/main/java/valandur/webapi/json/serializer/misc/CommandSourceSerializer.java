package valandur.webapi.json.serializer.misc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.command.CommandSource;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class CommandSourceSerializer extends WebAPISerializer<CommandSource> {
    @Override
    public void serialize(CommandSource value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        writeValue(provider, value.getName());
    }
}
