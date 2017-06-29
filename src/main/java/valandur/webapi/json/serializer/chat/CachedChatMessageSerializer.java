package valandur.webapi.json.serializer.chat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.api.cache.chat.CachedChatMessage;
import valandur.webapi.api.json.WebAPISerializer;

import java.io.IOException;

public class CachedChatMessageSerializer extends WebAPISerializer<CachedChatMessage> {
    @Override
    public void serialize(CachedChatMessage value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "timestamp", value.getTimestamp());
        writeField(provider, "sender", value.getSender());
        writeField(provider, "message", value.getMessage());
        gen.writeEndObject();
    }
}
