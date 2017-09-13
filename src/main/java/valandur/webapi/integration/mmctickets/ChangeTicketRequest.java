package valandur.webapi.integration.mmctickets;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize
public class ChangeTicketRequest {

    @JsonDeserialize
    private String comment;
    public String getComment() {
        return comment;
    }

    @JsonDeserialize
    private Integer status;
    public Integer getStatus() {
        return status;
    }

    @JsonDeserialize
    private Integer notified;
    public Integer getNotified() {
        return notified;
    }
}
