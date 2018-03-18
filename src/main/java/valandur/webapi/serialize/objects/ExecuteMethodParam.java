package valandur.webapi.serialize.objects;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.World;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

import static valandur.webapi.util.Util.ParameterType;

@ApiModel("ExecuteMethodParam")
public class ExecuteMethodParam {

    private ParameterType type;
    @ApiModelProperty(value = "The type of the parameter", required = true)
    public ParameterType getType() {
        return type;
    }

    private String value;
    @ApiModelProperty(value = "The value of the parameter", required = true)
    public String getValue() {
        return value;
    }


    public Tuple<Class, Object> toObject()
            throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        String val = value;
        String[] vals = val.split(":");

        switch (type) {
            case INT:
            case INTEGER:
                return new Tuple<>(Integer.class, Integer.parseInt(val));
            case FLOAT:
                return new Tuple<>(Float.class, Float.parseFloat(val));
            case DOUBLE:
                return new Tuple<>(Double.class, Double.parseDouble(val));
            case BOOL:
            case BOOLEAN:
                return new Tuple<>(Boolean.class, Boolean.parseBoolean(val));
            case BYTE:
                return new Tuple<>(Byte.class, Byte.parseByte(val));
            case CHAR:
                return new Tuple<>(Character.class, val.charAt(0));
            case LONG:
                return new Tuple<>(Long.class, Long.parseLong(val));
            case SHORT:
                return new Tuple<>(Short.class, Short.parseShort(val));
            case STRING:
                return new Tuple<>(String.class, val);
            case CLASS:
                return new Tuple<>(Class.class, Class.forName(val));
            case ENUM:
                Class clazz = Class.forName(vals[0]);
                return new Tuple<Class, Object>(clazz, Enum.valueOf(clazz, vals[1]));

            case VECTOR3D:
                return new Tuple<>(Vector3d.class, new Vector3d(
                        Double.parseDouble(vals[0]), Double.parseDouble(vals[1]), Double.parseDouble(vals[2])));

            case VECTOR3I:
                return new Tuple<>(Vector3i.class, new Vector3i(
                        Integer.parseInt(vals[0]), Integer.parseInt(vals[1]), Integer.parseInt(vals[2])));

            case TEXT:
                return new Tuple<>(Text.class, Text.of(val));

            case WORLD:
                Optional<World> w = Sponge.getServer().getWorld(UUID.fromString(val));
                return new Tuple<>(World.class, w.orElse(null));

            case PLAYER:
                Optional<Player> p = Sponge.getServer().getPlayer(UUID.fromString(val));
                return new Tuple<>(Player.class, p.orElse(null));

            case ITEMSTACK:
                Optional<ItemType> t = Sponge.getRegistry().getType(ItemType.class, vals[0]);
                if (!t.isPresent())
                    throw new ClassNotFoundException(vals[0]);

                return new Tuple<>(ItemStack.class, ItemStack.of(t.get(), Integer.parseInt(vals[1])));

            case STATIC:
                Class c = Class.forName(vals[0]);
                Field f = c.getField(vals[1]);
                return new Tuple<>(f.getType(), f.get(null));

            default:
                return null;
        }
    }
}
