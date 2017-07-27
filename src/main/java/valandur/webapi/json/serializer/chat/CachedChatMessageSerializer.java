package valandur.webapi.json.serializer.chat;

import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.cache.chat.CachedChatMessage;

import java.io.IOException;

public class CachedChatMessageSerializer extends WebAPIBaseSerializer<CachedChatMessage> {
    @Override
    public void serialize(CachedChatMessage value) throws IOException {
        writeStartObject();
        writeField("timestamp", value.getTimestamp());
        writeField("sender", value.getSender());
        writeField("message", value.getMessage());
        writeEndObject();
    }
}
