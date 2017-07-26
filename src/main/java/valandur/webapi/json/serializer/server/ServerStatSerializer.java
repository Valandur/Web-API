package valandur.webapi.json.serializer.server;

import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.server.ServerStat;

import java.io.IOException;

public class ServerStatSerializer extends WebAPIBaseSerializer<ServerStat<?>> {

    @Override
    public void serialize(ServerStat<?> value) throws IOException {
        writeStartObject();
        writeField("timestamp", value.getTimestamp());
        writeField("value", value.getValue());
        writeEndObject();
    }
}
