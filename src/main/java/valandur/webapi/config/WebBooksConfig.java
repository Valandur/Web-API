package valandur.webapi.config;

import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import valandur.webapi.integration.webbooks.WebBook;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class WebBooksConfig extends BaseConfig {

    public Map<String, WebBook> books = new HashMap<>();
}
