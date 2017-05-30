package valandur.webapi.hook;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class CommandWebHook {

    @Setting
    private boolean enabled = false;
    public boolean isEnabled() {
        return enabled;
    }

    @Setting
    private List<String> aliases;
    public List<String> getAliases() {
        return aliases;
    }

    @Setting
    private List<WebHookParam> params = new ArrayList<>();
    public List<WebHookParam> getParams() {
        return params;
    }

    @Setting
    private List<WebHook> hooks = new ArrayList<>();
    public List<WebHook> getHooks() { return hooks; }

    public CommandWebHook() {}
}
