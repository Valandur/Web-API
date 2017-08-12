package valandur.webapi.json.serializer.player;

import org.spongepowered.api.entity.living.player.User;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class UserSerializer extends WebAPIBaseSerializer<User> {

    @Override
    protected void serialize(User value) throws IOException {
        writeStartObject();

        writeField("uuid", value.getUniqueId());
        writeField("name", value.getName());

        if (shouldWriteDetails()) {
            writeField("class", value.getClass().getName());

            if (writeObjectFieldStart("armour")) {
                writeField("helmet", value.getHelmet());
                writeField("chestplate", value.getChestplate());
                writeField("leggings", value.getLeggings());
                writeField("boots", value.getBoots());
                writeEndObject();
            }

            writeField("inventory", value.getInventory());

            writeData(value);
        }

        writeEndObject();
    }
}
