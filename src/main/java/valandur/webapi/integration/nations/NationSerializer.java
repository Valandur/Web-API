/*package valandur.webapi.integration.nations;

import com.arckenver.nations.object.Nation;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class NationSerializer extends WebAPIBaseSerializer<Nation> {

    @Override
    protected void serialize(Nation value) throws IOException {
        writeStartObject();
        writeField("uuid", value.getUUID());
        writeField("name", value.getName());
        writeField("president", value.getPresident());

        if (shouldWriteDetails()) {
            writeField("realName", value.getRealName());
            writeField("citizens", value.getCitizens());
            writeField("ministers", value.getMinisters());
            writeField("staff", value.getStaff());
            writeField("upkeep", value.getUpkeep());
            writeField("taxes", value.getTaxes());
            writeField("flags", value.getFlags());
        }

        writeEndObject();
    }
}
*/