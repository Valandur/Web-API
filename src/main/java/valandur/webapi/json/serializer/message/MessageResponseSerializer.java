package valandur.webapi.json.serializer.message;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.json.serializer.WebAPISerializer;
import valandur.webapi.message.MessageResponse;

import java.io.IOException;

public class MessageResponseSerializer extends WebAPISerializer<MessageResponse> {
    @Override
    public void serialize(MessageResponse value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "id", value.getId());
        writeField(provider, "choice", value.getChoice());
        writeField(provider, "source", value.getSource());
        gen.writeEndObject();
    }
}
