package valandur.webapi.hook;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.player.ICachedPlayer;
import valandur.webapi.api.cache.world.ICachedWorld;

import java.util.Optional;

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
    private boolean optional = false;
    public boolean isOptional() { return optional; }


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

        if (optional)
            elem = GenericArguments.optional(elem);

        return Optional.of(elem);
    }

    public Optional<Object> getValue(CommandContext args) {
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
                return Optional.of(obj);

            case PLAYER:
                ICachedPlayer p = WebAPI.getCacheService().getPlayer((Player)obj);
                return Optional.of(p);

            case WORLD:
                ICachedWorld w = WebAPI.getCacheService().getWorld((World)obj);
                return Optional.of(w);
        }

        return Optional.empty();
    }
}
