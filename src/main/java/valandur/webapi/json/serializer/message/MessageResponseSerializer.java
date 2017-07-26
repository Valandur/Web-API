package valandur.webapi.json.serializer.message;

import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.message.MessageResponse;

import java.io.IOException;

public class MessageResponseSerializer extends WebAPIBaseSerializer<MessageResponse> {
    @Override
    public void serialize(MessageResponse value) throws IOException {
        writeStartObject();
        writeField("id", value.getId());
        writeField("choice", value.getChoice());
        writeField("source", value.getSource());
        writeEndObject();
    }
}
