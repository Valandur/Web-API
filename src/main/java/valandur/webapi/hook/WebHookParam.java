package valandur.webapi.hook;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.storage.WorldProperties;
import valandur.webapi.cache.player.CachedPlayer;
import valandur.webapi.cache.world.CachedWorld;
import valandur.webapi.cache.DataCache;

import java.util.Optional;
import java.util.UUID;

@ConfigSerializable
public class WebHookParam {

    public enum WebHookParamType {
        STRING, BOOL, INTEGER, DOUBLE, PLAYER, WORLD, LOCATION, VECTOR3D, VECTOR3I
    }

    @Setting
    private String name;
    public String getName() {
        return name;
    }

    @Setting
    private WebHookParamType type;
    public WebHookParamType getType() {
        return type;
    }

    @Setting
    private boolean isOptional = false;
    public boolean isOptional() { return isOptional; }


    public Optional<CommandElement> getCommandElement() {
        Text textName = Text.of(name);
        CommandElement elem = null;

        switch (type) {
            case STRING:
                elem = GenericArguments.string(textName);
                break;

            case BOOL:
                elem = GenericArguments.bool(textName);
                break;

            case INTEGER:
                elem = GenericArguments.integer(textName);
                break;

            case DOUBLE:
                elem = GenericArguments.doubleNum(textName);
                break;

            case PLAYER:
                elem = GenericArguments.player(textName);
                break;

            case WORLD:
                elem = GenericArguments.world(textName);
                break;

            case LOCATION:
                elem = GenericArguments.location(textName);
                break;

            case VECTOR3D:
                elem = GenericArguments.vector3d(textName);
                break;

            case VECTOR3I:
                elem = GenericArguments.vector3d(textName);
                break;
        }

        if (isOptional)
            elem = GenericArguments.optional(elem);

        return Optional.of(elem);
    }

    public Optional<Tuple<String, Object>> getValue(CommandContext args) {
        Optional<String> arg = args.getOne(name);
        if (!arg.isPresent()) return Optional.empty();

        Object obj = arg.get();

        switch (type) {
            case STRING:
            case BOOL:
            case INTEGER:
            case DOUBLE:
            case LOCATION:
            case VECTOR3D:
                return Optional.of(new Tuple<>(obj.toString(), obj));

            case PLAYER:
                UUID pUuid = ((Player)obj).getUniqueId();
                CachedPlayer p = DataCache.getPlayer(pUuid).orElse(null);
                return Optional.of(new Tuple<>(p.getUUID().toString(), p));

            case WORLD:
                UUID wUuid = ((WorldProperties)obj).getUniqueId();
                CachedWorld w = DataCache.getWorld(wUuid).orElse(null);
                return Optional.of(new Tuple<>(w.getUUID().toString(), w));
        }

        return Optional.empty();
    }
}
