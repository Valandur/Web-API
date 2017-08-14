package valandur.webapi.integration.huskycrates;

import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class CachedCrateRewardSerializer extends WebAPIBaseSerializer<CachedCrateReward> {

    @Override
    protected void serialize(CachedCrateReward value) throws IOException {
        writeStartObject();

        writeField("name", value.getName());
        writeField("chance", value.getChance());

        if (shouldWriteDetails()) {
            writeField("displayItem", value.getDisplayItem());
            writeField("shouldAnnounce", value.shouldAnnounce());
            writeField("rewards", value.getRewards());
        }

        writeEndObject();
    }
}
