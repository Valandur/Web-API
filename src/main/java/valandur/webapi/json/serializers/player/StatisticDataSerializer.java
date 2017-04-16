package valandur.webapi.json.serializers.player;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.data.manipulator.mutable.entity.StatisticData;
import org.spongepowered.api.statistic.Statistic;

import java.io.IOException;
import java.util.Map;

public class StatisticDataSerializer extends StdSerializer<StatisticData> {

    public StatisticDataSerializer() {
        this(null);
    }

    public StatisticDataSerializer(Class<StatisticData> t) {
        super(t);
    }

    @Override
    public void serialize(StatisticData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        Map<Statistic, Long> map = value.asMap();
        for (Map.Entry<Statistic, Long> entry : map.entrySet()) {
            gen.writeNumberField(entry.getKey().getId(), entry.getValue());
        }
        gen.writeEndObject();
    }
}
