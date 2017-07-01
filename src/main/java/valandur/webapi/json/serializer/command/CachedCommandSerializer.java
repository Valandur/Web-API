package valandur.webapi.json.serializer.command;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.api.cache.command.CachedCommand;
import valandur.webapi.api.json.WebAPISerializer;

import java.io.IOException;

public class CachedCommandSerializer extends WebAPISerializer<CachedCommand> {

    @Override
    public void serialize(CachedCommand value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "name", value.getName());
        writeField(provider, "description", value.getDescription());
        writeField(provider, "link", value.getLink());

        if (shouldWriteDetails(provider)) {
            writeField(provider, "aliases", value.getAliases());
            writeField(provider, "usage", value.getUsage());
            writeField(provider, "help", value.getHelp());
        }

        gen.writeEndObject();
    }
}
