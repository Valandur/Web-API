package valandur.webapi.integration.redprotect;

import org.spongepowered.api.util.Tristate;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class CachedRegionSerializer extends WebAPIBaseSerializer<CachedRegion> {

    @Override
    protected void serialize(CachedRegion value) throws IOException {
        writeStartObject();

        writeField("id", value.getId());
        writeField("name", value.getName());
        writeField("world", value.getWorld(), Tristate.FALSE);

        if (shouldWriteDetails()) {
            writeField("prior", value.getPrior());
            writeField("leaders", value.getLeaders(), Tristate.FALSE);
            writeField("admins", value.getAdmins(), Tristate.FALSE);
            writeField("members", value.getMembers(), Tristate.FALSE);
            writeField("welcomeMessage", value.getwMessage());
            writeField("date", value.getDate());
            writeField("flags", value.getFlags(), Tristate.FALSE);
            writeField("tpPoint", value.getTppoint(), Tristate.FALSE);
        }

        writeEndObject();
    }
}
