package valandur.webapi.json.serializer.block;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.world.extent.BlockVolume;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class BlockVolumeSerializer extends WebAPIBaseSerializer<BlockVolume> {
    @Override
    public void serialize(BlockVolume value) throws IOException {
        writeStartObject();

        writeField("min", value.getBlockMin());
        writeField("max", value.getBlockMax());

        writeArrayFieldStart("blocks");

        Vector3i min = value.getBlockMin();
        Vector3i max = value.getBlockMax();

        for (int x = min.getX(); x <= max.getX(); x++) {
            writeStartArray();

            for (int y = min.getY(); y <= max.getY(); y++) {
                writeStartArray();

                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    writeValue(value.getBlock(x, y, z));
                }

                writeEndArray();
            }

            writeEndArray();
        }

        writeEndArray();

        writeEndObject();
    }
}
