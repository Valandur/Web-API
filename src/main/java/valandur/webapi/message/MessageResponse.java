package valandur.webapi.message;

import java.util.UUID;

public class MessageResponse {
    private String id;
    public String getId() {
        return id;
    }

    private UUID source;
    public UUID getSource() {
        return source;
    }

    private String choice;
    public String getChoice() {
        return choice;
    }


    public MessageResponse(String id, String choice, UUID source) {
        this.id  = id;
        this.choice = choice;
        this.source = source;
    }
}
