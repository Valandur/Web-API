package valandur.webapi.hook;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class WebHookHeader {

    @Setting
    private String name;
    public String getName() {
        return name;
    }

    @Setting
    private String value;
    public String getValue() {
        return value;
    }
}
