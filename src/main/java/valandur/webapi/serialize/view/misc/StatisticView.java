package valandur.webapi.serialize.view.misc;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.statistic.Statistic;
import valandur.webapi.serialize.BaseView;

public class StatisticView extends BaseView<Statistic> {

    @JsonValue
    public String stat;


    public StatisticView(Statistic value) {
        super(value);

        this.stat = value.getId();
    }
}
