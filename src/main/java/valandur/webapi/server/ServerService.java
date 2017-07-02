package valandur.webapi.server;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.WebAPI;
import valandur.webapi.api.server.IServerService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ServerService implements IServerService {

    private Map<String, String> properties = new HashMap<>();
    private Map<String, String> newProperties = new HashMap<>();
    public Map<String, String> getProperties() {
        return newProperties;
    }

    // Record every 5 seconds. Max 17280 entries = 24 hours of tps
    public static int STATS_INTERVAL = 5;
    public static int MAX_STATS_ENTRIES = 4320;
    public List<Tuple<Instant, Double>> averageTps = new ArrayList<>();
    public List<Tuple<Instant, Integer>> onlinePlayers = new ArrayList<>();

    private Task tpsTask;


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

        if (tpsTask != null) {
            tpsTask.cancel();
        }

        tpsTask = Task.builder().execute(() -> {
            averageTps.add(0, new Tuple<>(Instant.now(), Sponge.getServer().getTicksPerSecond()));
            while (averageTps.size() > MAX_STATS_ENTRIES)
                averageTps.remove(averageTps.size() - 1);

            onlinePlayers.add(0, new Tuple<>(Instant.now(), Sponge.getServer().getOnlinePlayers().size()));
            while (onlinePlayers.size() > MAX_STATS_ENTRIES)
                onlinePlayers.remove(onlinePlayers.size() - 1);
        }).async().interval(STATS_INTERVAL, TimeUnit.SECONDS).name("Web-API - Average TPS").submit(WebAPI.getInstance());

        newProperties.putAll(properties);
    }

    public List<Tuple<Instant, Double>> getAverageTps() {
        return averageTps;
    }
    public List<Tuple<Instant, Integer>> getOnlinePlayers() {
        return onlinePlayers;
    }

    public void setProperty(String key, String value) {
        newProperties.put(key, value);
    }
}
