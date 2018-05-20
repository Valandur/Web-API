package valandur.webapi.server;

import com.sun.management.OperatingSystemMXBean;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import valandur.webapi.WebAPI;
import valandur.webapi.api.server.IServerService;
import valandur.webapi.api.server.IServerStat;
import valandur.webapi.config.ServerConfig;
import valandur.webapi.util.Util;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class ServerService implements IServerService {

    private final static String configFileName = "server.conf";

    // Record every 5 seconds. (17280 entries = 24 hours, 4320 entries = 6 hours)
    private static int STATS_INTERVAL = 5;
    private static int MAX_STATS_ENTRIES = 4320;

    private OperatingSystemMXBean systemMXBean;
    private Map<String, ServerProperty> properties = new ConcurrentHashMap<>();
    private Map<String, ServerProperty> newProperties = new ConcurrentHashMap<>();

    private Queue<ServerStat<Double>> averageTps = new ConcurrentLinkedQueue<>();
    private Queue<ServerStat<Integer>> onlinePlayers = new ConcurrentLinkedQueue<>();
    private Queue<ServerStat<Double>> cpuLoad = new ConcurrentLinkedQueue<>();
    private Queue<ServerStat<Double>> memoryLoad = new ConcurrentLinkedQueue<>();
    private Queue<ServerStat<Double>> diskUsage = new ConcurrentLinkedQueue<>();

    private Task statTask;


    public void init() {
        properties.clear();
        newProperties.clear();

        ServerConfig config = Util.loadConfig(configFileName, new ServerConfig());

        STATS_INTERVAL = config.statsInterval;
        MAX_STATS_ENTRIES = config.maxStats;

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

        newProperties.putAll(properties);

        if (statTask != null) {
            statTask.cancel();
        }

        systemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        statTask = Task.builder().execute(this::recordStats)
                .async()
                .delay(STATS_INTERVAL, TimeUnit.SECONDS)
                .interval(STATS_INTERVAL, TimeUnit.SECONDS)
                .name("Web-API - Server stats")
                .submit(WebAPI.getInstance());
    }

    private void recordStats() {
        long total = 0;
        long free = 0;
        File[] roots = File.listRoots();
        for (File root : roots) {
            total += root.getTotalSpace();
            free += root.getFreeSpace();
        }

        long totalMem = Runtime.getRuntime().totalMemory();
        long usedMem = (totalMem - Runtime.getRuntime().freeMemory());

        // Stuff accessing sponge needs to be run on the server main thread
        WebAPI.runOnMain(() -> {
            averageTps.add(new ServerStat<>(Sponge.getServer().getTicksPerSecond()));
            onlinePlayers.add(new ServerStat<>(Sponge.getServer().getOnlinePlayers().size()));
        });
        cpuLoad.add(new ServerStat<>(systemMXBean.getProcessCpuLoad()));
        memoryLoad.add(new ServerStat<>((totalMem - usedMem) / (double)totalMem));
        diskUsage.add(new ServerStat<>((total - free) / (double)total));

        while (averageTps.size() > MAX_STATS_ENTRIES)
            averageTps.poll();
        while (onlinePlayers.size() > MAX_STATS_ENTRIES)
            onlinePlayers.poll();
        while (cpuLoad.size() > MAX_STATS_ENTRIES)
            cpuLoad.poll();
        while (memoryLoad.size() > MAX_STATS_ENTRIES)
            memoryLoad.poll();
        while (diskUsage.size() > MAX_STATS_ENTRIES)
            diskUsage.poll();
    }

    @Override
    public int getNumEntries() {
        return Math.min(averageTps.size(), Math.min(onlinePlayers.size(), Math.min(cpuLoad.size(),
                Math.min(memoryLoad.size(), diskUsage.size()))));
    }

    @Override
    public List<IServerStat<Double>> getAverageTps() {
        return new ArrayList<>(averageTps);
    }
    @Override
    public List<IServerStat<Integer>> getOnlinePlayers() {
        return new ArrayList<>(onlinePlayers);
    }
    @Override
    public List<IServerStat<Double>> getCpuLoad() {
        return new ArrayList<>(cpuLoad);
    }
    @Override
    public List<IServerStat<Double>> getMemoryLoad() {
        return new ArrayList<>(memoryLoad);
    }
    @Override
    public List<IServerStat<Double>> getDiskUsage() {
        return new ArrayList<>(diskUsage);
    }

    public Collection<ServerProperty> getProperties() {
        return newProperties.values();
    }
    public void setProperty(String key, String value) {
        newProperties.put(key, new ServerProperty(key, value));
    }
    public void saveProperties() throws IOException {
        Path path = Paths.get("./server.properties");
        Path backupPath = Paths.get("./server.properties.bck");
        if (!Files.exists(backupPath)) {
            Files.copy(path, backupPath);
        }

        StringBuilder text = new StringBuilder("#Minecraft server properties\n");
        text.append("#Modified by Web-API\n");
        text.append("#").append((new Date()).toString()).append("\n");
        for (ServerProperty prop : newProperties.values()) {
            text.append(prop.getKey()).append("=").append(prop.getValue()).append("\n");
        }
        Files.write(path, text.toString().getBytes(Charset.forName("utf-8")));
    }
}
