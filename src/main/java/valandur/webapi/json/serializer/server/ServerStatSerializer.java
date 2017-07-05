package valandur.webapi.json.serializer.server;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.server.ServerStat;

import java.io.IOException;

public class ServerStatSerializer extends WebAPIBaseSerializer<ServerStat<?>> {

    @Override
    public void serialize(ServerStat<?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "timestamp", value.getTimestamp());
        writeField(provider, "value", value.getValue());
        gen.writeEndObject();
    }
}
