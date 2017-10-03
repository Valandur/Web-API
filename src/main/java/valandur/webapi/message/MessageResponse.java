package valandur.webapi.message;

public class MessageResponse {

    private String id;
    public String getId() {
        return id;
    }

    private String source;
    public String getSource() {
        return source;
    }

    private String choice;
    public String getChoice() {
        return choice;
    }


    public MessageResponse(String id, String choice, String source) {
        this.id  = id;
        this.choice = choice;
        this.source = source;
    }
}
