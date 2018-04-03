package valandur.webapi.message;

public class InteractiveMessageResponse {

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


    public InteractiveMessageResponse(String id, String choice, String source) {
        this.id  = id;
        this.choice = choice;
        this.source = source;
    }
}
