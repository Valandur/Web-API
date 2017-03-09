package valandur.webapi.misc;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

public class ParamDef {
    public Class type;
    public Object value;

    public ParamDef(Class type, Object value) {
        this.type = type;
        this.value = value;
    }

    public static ParamDef fromJson(JsonObject json) throws ClassNotFoundException {
        String type = json.get("type").getAsString().toLowerCase();
        JsonElement e = json.get("value");

        switch (type) {
            case "int":
            case "integer":
                return new ParamDef(int.class, e.getAsInt());
            case "float":
                return new ParamDef(float.class, e.getAsFloat());
            case "double":
                return new ParamDef(double.class, e.getAsDouble());
            case "bool":
            case "boolean":
                return new ParamDef(boolean.class, e.getAsBoolean());
            case "byte":
                return new ParamDef(byte.class, e.getAsByte());
            case "char":
                return new ParamDef(char.class, e.getAsCharacter());
            case "long":
                return new ParamDef(long.class, e.getAsLong());
            case "short":
                return new ParamDef(short.class, e.getAsShort());
            case "string":
                return new ParamDef(String.class, e.getAsString());
            case "class":
                return new ParamDef(Class.class, Class.forName(type));

            case "text":
                return new ParamDef(Text.class, Text.of(e.getAsString()));

            case "world":
                Optional<World> w = Sponge.getServer().getWorld(UUID.fromString(e.getAsString()));
                return new ParamDef(World.class, w.orElse(null));

            case "player":
                Optional<Player> p = Sponge.getServer().getPlayer(UUID.fromString(e.getAsString()));
                return new ParamDef(World.class, p.orElse(null));

            case "itemstack":
                JsonObject sub = e.getAsJsonObject();
                String cName = sub.get("itemType").getAsString();
                Optional<ItemType> t = Sponge.getRegistry().getType(ItemType.class, cName);
                int amount = sub.get("amount").getAsInt();

                if (!t.isPresent())
                    throw new ClassNotFoundException(cName);

                return new ParamDef(ItemStack.class, ItemStack.of(t.get(), amount));

            default:
                return new ParamDef(Class.forName(type), null);
        }
    }
}
