package valandur.webapi.api.service;

import java.util.Map;

public interface IServerService {

    Map<String, String> getProperties();

    void setProperty(String key, String value);
}
