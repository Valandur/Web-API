package valandur.webapi.hooks;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class WebHookHeader {

    @Setting(comment = "The name of the header")
    private String name;
    public String getName() {
        return name;
    }

    @Setting(comment = "The value of the header")
    private String value;
    public String getValue() {
        return value;
    }
}
