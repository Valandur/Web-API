package valandur.webapi.integration.mmctickets;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.moddedminecraft.mmctickets.data.ticketStatus;

@JsonDeserialize
public class ChangeTicketRequest {

    @JsonDeserialize
    private String comment;
    public String getComment() {
        return comment;
    }

    @JsonDeserialize
    private ticketStatus status;
    public ticketStatus getStatus() {
        return status;
    }

    @JsonDeserialize
    private Integer notified;
    public Integer getNotified() {
        return notified;
    }
}
