package valandur.webapi.hook;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class CommandWebHook {

    @Setting(comment = "If set to false this disables the complete command hook and also doesn't\n" +
            "register the command in the command registry")
    private boolean enabled = false;
    public boolean isEnabled() {
        return enabled;
    }

    @Setting(comment = "The aliases this command can be called with.\n" +
            "THESE ARE NOT PREFIXED WITH \"/webapi notify\", BE CAREFUL NOT TO OVERRIDE OTHER COMMANDS! (unless you want to)")
    private List<String> aliases;
    public List<String> getAliases() {
        return aliases;
    }

    @Setting(comment = "Here you have to specify what parameters are used.\n" +
            "These parameters must be included when running the command. So the example command below would have to be\n" +
            "run using the following syntax: /webapi notify test {player} {world}\n" +
            "\n" +
            "The body automatically contains all the parameters that are listed here. The parameters are expected to\n" +
            "show up in this order when calling the command. There is a special parameter 'source' which is always\n" +
            "included and contains the identifier of the one who ran the command (e.g. UUID of the player).\n" +
            "Possible parameter types are: STRING, BOOL, INTEGER, DOUBLE, PLAYER, WORLD, LOCATION, VECTOR3D, VECTOR3I.\n" +
            "Parameters can be marked as optional, and if set to true will be wrapped in an optional arguments.")
    private List<WebHookParam> params = new ArrayList<>();
    public List<WebHookParam> getParams() {
        return params;
    }

    @Setting(comment = "The list of hooks that is called when this command is executed\n" +
            "The same kind of hooks as in the events can be used, with the params as {param} in the URL or headers.")
    private List<WebHook> hooks = new ArrayList<>();
    public List<WebHook> getHooks() { return hooks; }


    public CommandWebHook() {}
    public CommandWebHook(boolean enabled, List<String> aliases, List<WebHookParam> params, List<WebHook> hooks) {
        this.enabled = enabled;
        this.aliases = aliases;
        this.params = params;
        this.hooks = hooks;
    }
}
