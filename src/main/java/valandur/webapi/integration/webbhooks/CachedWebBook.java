package valandur.webapi.integration.webbhooks;

public class CachedWebBook {

    private String id;
    public String getId() {
        return id;
    }

    private String body;
    public String getBody() {
        return body;
    }


    public CachedWebBook(String id, String body) {
        this.id = id;
        this.body = body;
    }
}
