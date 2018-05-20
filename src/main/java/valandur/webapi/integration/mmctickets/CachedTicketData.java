package valandur.webapi.integration.mmctickets;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.moddedminecraft.mmctickets.data.TicketData;
import net.moddedminecraft.mmctickets.data.ticketStatus;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.player.CachedPlayer;
import valandur.webapi.cache.world.CachedLocation;
import valandur.webapi.serialize.JsonDetails;
import valandur.webapi.util.Constants;

@ApiModel("MMCTicketsTicket")
public class CachedTicketData extends CachedObject<TicketData> {

    private int id;
    @ApiModelProperty(value = "The unique id of this ticket", required = true)
    public int getId() {
        return id;
    }

    private long timestamp;
    @ApiModelProperty(value = "The unix timestamp (in seconds) when this ticket was submitted", required = true)
    public long getTimestamp() {
        return timestamp;
    }

    private ticketStatus status;
    @ApiModelProperty(value = "The current status of the ticket", required = true)
    public ticketStatus getStatus() {
        return status;
    }

    private String message;
    @ApiModelProperty(value = "The message sent along with this ticket", required = true)
    public String getMessage() {
        return message;
    }

    private CachedPlayer sender;
    @JsonDetails
    @ApiModelProperty("The sender of this ticket")
    public CachedPlayer getSender() {
        return sender;
    }

    private String comment;
    @JsonDetails
    @ApiModelProperty("The comment added by staff to this ticket")
    public String getComment() {
        return comment;
    }

    private CachedPlayer staff;
    @JsonDetails
    @ApiModelProperty("The staff member that was assigned to this ticket")
    public CachedPlayer getStaff() {
        return staff;
    }

    private Integer notified;
    @JsonDetails
    @ApiModelProperty("True if staff has been notified about this ticket, false otherwise")
    public Integer getNotified() {
        return notified;
    }

    private CachedLocation location;
    @JsonDetails
    @ApiModelProperty("The location at which this ticket was submitted")
    public CachedLocation getLocation() {
        return location;
    }


    public CachedTicketData() {
        super(null);
    }
    public CachedTicketData(TicketData ticket) {
        super(ticket);

        this.id = ticket.getTicketID();
        this.sender = cacheService.getPlayer(ticket.getPlayerUUID()).orElse(null);
        this.timestamp = ticket.getTimestamp();
        this.message = ticket.getMessage();
        this.comment = ticket.getComment();
        this.status = ticket.getStatus();
        this.staff = cacheService.getPlayer(ticket.getPlayerUUID()).orElse(null);
        this.notified = ticket.getNotified();
        this.location = new CachedLocation(ticket.getWorld(), ticket.getX(), ticket.getY(), ticket.getZ());
    }

    @Override
    public String getLink() {
        return Constants.BASE_PATH + "/mmc-tickets/ticket/" + id;
    }
}
