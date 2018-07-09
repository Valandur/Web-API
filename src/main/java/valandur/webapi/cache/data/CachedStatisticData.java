package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.StatisticData;
import org.spongepowered.api.statistic.Statistic;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;

import java.util.List;
import java.util.stream.Collectors;

public class CachedStatisticData extends CachedObject<StatisticData> {

    private List<Stat> statistics;
    @JsonValue
    public List<Stat> getStatistics() {
        return statistics;
    }

    public CachedStatisticData(StatisticData value) {
        super(value);

        this.statistics = value.getMapValues().stream()
                .map(mv -> new Stat(new CachedCatalogType<>(mv.getKey()), mv.getValue()))
                .collect(Collectors.toList());
    }


    public static class Stat {
        public CachedCatalogType<Statistic> stat;
        public Long value;

        public Stat(CachedCatalogType<Statistic> stat, Long value) {
            this.stat = stat;
            this.value = value;
        }
    }
}
