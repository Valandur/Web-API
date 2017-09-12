package valandur.webapi.integration.webbhooks;

import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class CachedWebBookSerializer extends WebAPIBaseSerializer<CachedWebBook> {

    @Override
    protected void serialize(CachedWebBook value) throws IOException {
        writeStartObject();

        writeField("id", value.getId());

        if (shouldWriteDetails()) {
            writeField("body", value.getBody());
        }

        writeEndObject();
    }
}
