package valandur.webapi.api.service;

import org.spongepowered.api.util.Tuple;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface IServerService {

    Map<String, String> getProperties();

    void setProperty(String key, String value);

    List<Tuple<Instant, Double>> getAverageTps();
    List<Tuple<Instant, Integer>> getOnlinePlayers();
}
