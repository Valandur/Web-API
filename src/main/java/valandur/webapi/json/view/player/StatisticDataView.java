package valandur.webapi.json.view.player;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.StatisticData;
import org.spongepowered.api.statistic.Statistic;
import valandur.webapi.api.json.BaseView;

import java.util.Map;

public class StatisticDataView extends BaseView<StatisticData> {

    @JsonValue
    public Map<String, Long> data;


    public StatisticDataView(StatisticData value) {
        super(value);

        Map<Statistic, Long> map = value.asMap();
        for (Map.Entry<Statistic, Long> entry : map.entrySet()) {
            data.put(entry.getKey().getId(), entry.getValue());
        }
    }
}
