package valandur.webapi.json.serializer.misc;

import org.spongepowered.api.command.CommandSource;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class CommandSourceSerializer extends WebAPIBaseSerializer<CommandSource> {
    @Override
    public void serialize(CommandSource value) throws IOException {
        writeStartObject();
        writeField("id", value.getIdentifier());
        writeField("name", value.getName());
        writeEndObject();
    }
}
