package valandur.webapi.json.serializer.misc;

import org.spongepowered.api.util.Tuple;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class TupleSerializer extends WebAPIBaseSerializer<Tuple> {

    @Override
    public void serialize(Tuple value) throws IOException {
        writeStartObject();
        writeField("first", value.getFirst());
        writeField("second", value.getSecond());
        writeEndObject();
    }
}
