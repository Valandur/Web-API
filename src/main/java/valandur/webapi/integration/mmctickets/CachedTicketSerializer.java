package valandur.webapi.integration.mmctickets;

import org.spongepowered.api.util.Tristate;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class CachedTicketSerializer extends WebAPIBaseSerializer<CachedTicket> {

    @Override
    protected void serialize(CachedTicket value) throws IOException {
        writeStartObject();

        writeField("id", value.getId());
        writeField("timestamp", value.getTimestamp());

        if (shouldWriteDetails()) {
            writeField("sender", value.getSender(), Tristate.FALSE);
            writeField("message", value.getMessage());
            writeField("comment", value.getComment());
            writeField("status", value.getStatus());
            writeField("location", value.getLocation(), Tristate.FALSE);
            writeField("staff", value.getStaff(), Tristate.FALSE);
            writeField("notified", value.getNotified());
        }

        writeEndObject();
    }
}
