package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.StatisticData;
import org.spongepowered.api.statistic.Statistic;
import valandur.webapi.serialize.BaseView;

import java.util.List;
import java.util.stream.Collectors;

public class StatisticDataView extends BaseView<StatisticData> {

    @JsonValue
    public List<Stat> getStatistics() {
        return value.statistics().entrySet().stream()
                .map(mv -> new Stat(mv.getKey(), mv.getValue()))
                .collect(Collectors.toList());
    }

    public StatisticDataView(StatisticData value) {
        super(value);
    }


    public static class Stat {
        public Statistic stat;
        public Long value;

        public Stat(Statistic stat, Long value) {
            this.stat = stat;
            this.value = value;
        }
    }
}
