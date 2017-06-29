package valandur.webapi.services;

import valandur.webapi.api.service.IServerService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerService implements IServerService {

    private Map<String, String> properties = new HashMap<>();
    private Map<String, String> newProperties = new HashMap<>();
    public Map<String, String> getProperties() {
        return newProperties;
    }


    public void init() {
        properties.clear();

        try {
            List<String> lines = Files.readAllLines(Paths.get("./server.properties"));
            for (String line : lines) {
                String[] splits = line.split("=");
                String key = splits[0].trim();
                if (key.startsWith("#")) {
                    continue;
                }

                properties.put(key, splits.length > 1 ? splits[1].trim() : null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        newProperties.putAll(properties);
    }

    public void setProperty(String key, String value) {
        newProperties.put(key, value);
    }
}
