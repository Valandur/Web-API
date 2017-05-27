package valandur.webapi.json.serializer.block;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.world.extent.BlockVolume;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class BlockVolumeSerializer extends WebAPISerializer<BlockVolume> {
    @Override
    public void serialize(BlockVolume value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        writeField(provider, "min", value.getBlockMin());
        writeField(provider, "max", value.getBlockMax());

        gen.writeArrayFieldStart("blocks");

        Vector3i min = value.getBlockMin();
        Vector3i max = value.getBlockMax();

        for (int x = min.getX(); x <= max.getX(); x++) {
            gen.writeStartArray();

            for (int y = min.getY(); y <= max.getY(); y++) {
                gen.writeStartArray();

                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    writeValue(provider, value.getBlock(x, y, z));
                }

                gen.writeEndArray();
            }

            gen.writeEndArray();
        }

        gen.writeEndArray();

        gen.writeEndObject();
    }
}
