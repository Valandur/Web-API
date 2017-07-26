package valandur.webapi.json.serializer.tileentity;

import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.text.Text;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class SignDataSerializer extends WebAPIBaseSerializer<SignData> {
    @Override
    public void serialize(SignData value) throws IOException {
        writeStartArray();
        for (Text line : value.asList()) {
            writeValue(line.toPlain());
        }
        writeEndArray();
    }
}
