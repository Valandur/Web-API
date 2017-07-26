package valandur.webapi.json.serializer.command;

import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.cache.command.CachedCommandCall;

import java.io.IOException;

public class CachedCommandCallSerializer extends WebAPIBaseSerializer<CachedCommandCall> {
    @Override
    public void serialize(CachedCommandCall value) throws IOException {
        writeStartObject();
        writeField("class", value.getClass().getName());
        writeField("timestamp", value.getTimestamp());
        writeField("command", value.getCommand());
        writeField("cause", value.getCause());
        writeField("args", value.getArgs());
        writeField("cancelled", value.isCancelled());
        writeField("result", value.getResult());
        writeEndObject();
    }
}
