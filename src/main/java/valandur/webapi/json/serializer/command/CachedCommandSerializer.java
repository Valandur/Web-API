package valandur.webapi.json.serializer.command;

import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.cache.command.CachedCommand;

import java.io.IOException;

public class CachedCommandSerializer extends WebAPIBaseSerializer<CachedCommand> {

    @Override
    public void serialize(CachedCommand value) throws IOException {
        writeStartObject();
        writeField("name", value.getName());
        writeField("description", value.getDescription());
        writeField("link", value.getLink());

        if (shouldWriteDetails()) {
            writeField("aliases", value.getAliases());
            writeField("usage", value.getUsage());
            writeField("help", value.getHelp());
        }

        writeEndObject();
    }
}
