package valandur.webapi.json.serializer.command;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.cache.command.CachedCommandCall;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class CachedCommandCallSerializer extends WebAPISerializer<CachedCommandCall> {
    @Override
    public void serialize(CachedCommandCall value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "class", value.getClass().getName());
        writeField(provider, "timestamp", value.getTimestamp());
        writeField(provider, "command", value.getCommand());
        writeField(provider, "cause", value.getCause());
        writeField(provider, "args", value.getArgs());
        writeField(provider, "cancelled", value.isCancelled());
        writeField(provider, "result", value.getResult());
        gen.writeEndObject();
    }
}
