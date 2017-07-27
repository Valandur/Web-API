package valandur.webapi.json.serializer.player;

import org.spongepowered.api.data.manipulator.mutable.entity.StatisticData;
import org.spongepowered.api.statistic.Statistic;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;
import java.util.Map;

public class StatisticDataSerializer extends WebAPIBaseSerializer<StatisticData> {
    @Override
    public void serialize(StatisticData value) throws IOException {
        writeStartObject();
        Map<Statistic, Long> map = value.asMap();
        for (Map.Entry<Statistic, Long> entry : map.entrySet()) {
            writeField(entry.getKey().getId(), entry.getValue());
        }
        writeEndObject();
    }
}
