package valandur.webapi.server;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import valandur.webapi.WebAPI;
import valandur.webapi.api.server.IServerService;
import valandur.webapi.api.server.IServerStat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class ServerService implements IServerService {

    private Map<String, ServerProperty> properties = new ConcurrentHashMap<>();
    private Map<String, ServerProperty> newProperties = new ConcurrentHashMap<>();

    // Record every 5 seconds. Max 17280 entries = 24 hours of tps
    public static int STATS_INTERVAL = 5;
    public static int MAX_STATS_ENTRIES = 4320;
    public Queue<ServerStat<Double>> averageTps = new ConcurrentLinkedQueue<>();
    public Queue<ServerStat<Integer>> onlinePlayers = new ConcurrentLinkedQueue<>();

    private Task statTask;


    public void init() {
        properties.clear();
        newProperties.clear();

        try {
            List<String> lines = Files.readAllLines(Paths.get("./server.properties"));
            for (String line : lines) {
                String[] splits = line.split("=");
                String key = splits[0].trim();
                if (key.startsWith("#")) {
                    continue;
                }

                properties.put(key, new ServerProperty(key, splits.length > 1 ? splits[1].trim() : ""));
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (WebAPI.reportErrors()) WebAPI.sentryCapture(e);
        }

        if (statTask != null) {
            statTask.cancel();
        }

        statTask = Task.builder().execute(() -> {
            WebAPI.runOnMain(() -> {
                averageTps.add(new ServerStat<>(Sponge.getServer().getTicksPerSecond()));
                onlinePlayers.add(new ServerStat<>(Sponge.getServer().getOnlinePlayers().size()));
            });

            while (averageTps.size() > MAX_STATS_ENTRIES)
                averageTps.poll();
            while (onlinePlayers.size() > MAX_STATS_ENTRIES)
                onlinePlayers.poll();
        }).async().interval(STATS_INTERVAL, TimeUnit.SECONDS).name("Web-API - Average TPS").submit(WebAPI.getInstance());

        newProperties.putAll(properties);
    }

    @Override
    public List<IServerStat<Double>> getAverageTps() {
        return new ArrayList<>(averageTps);
    }
    @Override
    public List<IServerStat<Integer>> getOnlinePlayers() {
        return new ArrayList<>(onlinePlayers);
    }

    public Collection<ServerProperty> getProperties() {
        return newProperties.values();
    }
    public void setProperty(String key, String value) {
        newProperties.put(key, new ServerProperty(key, value));
    }
}
