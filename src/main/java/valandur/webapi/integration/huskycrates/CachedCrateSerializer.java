package valandur.webapi.integration.huskycrates;

import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class CachedCrateSerializer extends WebAPIBaseSerializer<CachedCrate> {

    @Override
    protected void serialize(CachedCrate value) throws IOException {
        writeStartObject();

        writeField("id", value.getId());
        writeField("name", value.getName());
        writeField("type", value.getType());

        if (shouldWriteDetails()) {
            writeField("isFree", value.isFree());
            writeField("keys", value.getKeys());
            writeField("rewards", value.getRewards());
        }

        writeEndObject();
    }
}
