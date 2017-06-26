package valandur.webapi.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerProperties {

    private static Map<String, String> properties = new HashMap<>();
    private static Map<String, String> newProperties = new HashMap<>();
    public static Map<String, String> getProperties() {
        return newProperties;
    }

    public static void init() {
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

    public static void setProperty(String key, String value) {
        newProperties.put(key, value);
    }
}
