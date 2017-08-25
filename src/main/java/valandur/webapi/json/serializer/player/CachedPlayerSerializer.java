package valandur.webapi.json.serializer.player;

import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.cache.player.CachedPlayer;

import java.io.IOException;

public class CachedPlayerSerializer extends WebAPIBaseSerializer<CachedPlayer> {
    @Override
    public void serialize(CachedPlayer value) throws IOException {
        writeStartObject();

        writeField("uuid", value.getUUID());
        writeField("name", value.getName());
        writeField("type", value.getType());
        writeField("link", value.getLink());
        writeField("location", value.getLocation());

        if (shouldWriteDetails()) {
            writeField("rotation", value.getRotation());
            writeField("velocity", value.getVelocity());
            writeField("scale", value.getScale());

            if (writeObjectFieldStart("connection")) {
                writeField("latency", value.getLatency());
                writeField("address", value.getAddress());
                writeEndObject();
            }

            if (writeObjectFieldStart("armour")) {
                writeField("helmet", value.getHelmet());
                writeField("chestplate", value.getChestplate());
                writeField("leggings", value.getLeggings());
                writeField("boots", value.getBoots());
                writeEndObject();
            }

            writeField("inventory", value.getInventory());

            writeData(value.getData());
        }

        writeEndObject();
    }
}
