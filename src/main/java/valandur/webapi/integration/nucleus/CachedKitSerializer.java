package valandur.webapi.integration.nucleus;

import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class CachedKitSerializer extends WebAPIBaseSerializer<CachedKit> {

    @Override
    protected void serialize(CachedKit value) throws IOException {
        writeStartObject();
        writeField("name", value.getName());
        writeField("cost", value.getCost());
        writeField("interval", value.getInterval().getSeconds());
        writeField("isFirstJoin", value.isFirstJoinKit());
        writeField("oneTime", value.isOneTime());
        writeField("commands", value.getCommands());
        writeField("stacks", value.getStacks());
        writeEndObject();
    }
}
