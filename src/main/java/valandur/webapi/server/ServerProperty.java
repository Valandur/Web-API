package valandur.webapi.server;

public class ServerProperty {

    private String key;
    public String getKey() {
        return key;
    }

    private String value;
    public String getValue() {
        return value;
    }


    public ServerProperty(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
