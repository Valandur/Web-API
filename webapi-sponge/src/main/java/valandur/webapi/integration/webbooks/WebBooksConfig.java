package valandur.webapi.integration.webbooks;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import valandur.webapi.config.BaseConfig;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class WebBooksConfig extends BaseConfig {

    @Setting(comment = "A map of book ids to books")
    public Map<String, WebBook> books = new HashMap<>();
}
